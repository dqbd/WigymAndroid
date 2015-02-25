package cz.duong.wigym.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import cz.duong.wigym.R;
import cz.duong.wigym.data.ArticleData;
import cz.duong.wigym.interfaces.NewsListener;
import cz.duong.wigym.persistency.UpdateContainer;
import cz.duong.wigym.ui.activities.ArticleActivity;
import cz.duong.wigym.ui.activities.MainActivity;
import cz.duong.wigym.ui.adapters.NewsAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

public class NewsFragment extends Fragment implements NewsListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView mList;
    private SwipeRefreshLayout refreshLayout;

    private Realm db;
    private UpdateContainer updater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.news_fragment, container, false);

        mList = (ListView) main.findViewById(R.id.news_listview);
        refreshLayout = (SwipeRefreshLayout) main.findViewById(R.id.refresh_layout);

        refreshLayout.setColorSchemeResources(R.color.blue);
        refreshLayout.setOnRefreshListener(this);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticleActivity.class);
                intent.putExtra(ArticleActivity.ARTICLE_ID, ((NewsAdapter) adapterView.getAdapter()).getItem(i).getId());

                getActivity().startActivity(intent);
            }
        });

        db = Realm.getInstance(this.getActivity());
        updater = new UpdateContainer(db);

        onDataLoaded(false, false);

        return main;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(R.string.fragment_mews);
    }

    @Override
    public void OnPreLoad() {}

    @Override
    public void OnLoaded(List<Article> articles) {
        if(articles.size() > 0) {
            db.beginTransaction();

            db.where(ArticleData.class).findAll().clear(); //delete all current articles

            for(Article article : articles) {
                ArticleData item = db.createObject(ArticleData.class);

                if(article.getImage().toString().length() == 0) {
                    Document doc = Jsoup.parse(article.getContent());

                    Elements images = doc.select("a[href~=\\.(png|jpe?g|gif)$]");

                    if(images.size() > 0) {
                        item.setImage(images.first().attr("href"));
                    } else {
                        images = doc.select("img[src~=\\.(png|jpe?g|gif)$]:not(.wp-smiley)");

                        if(images.size() > 0) {
                            item.setImage(images.first().attr("src"));
                        }
                    }
                } else {
                    item.setImage(article.getImage().toString());
                }

                item.setSource(article.getSource().toString());
                item.setTitle(article.getTitle());
                item.setDescription(article.getDescription());
                item.setContent(article.getContent());
                item.setComments(article.getComments());
                item.setAuthor(article.getAuthor());
                item.setDate(article.getDate());
                item.setId(article.getId());
            }

            db.commitTransaction();

            onDataLoaded(true, true);
        } else {
            this.OnLoadFailed();
        }
    }

    @Override
    public void OnLoadFailed() {
        onDataLoaded(true, false);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 1000);

        PkRSS.with(this.getActivity()).load(getResources().getString(R.string.news_url)).callback(this).async();
    }

    @Override
    public void onDataLoaded(boolean updated, boolean success) {
        if(db != null) {
            final RealmResults<ArticleData> results = db.where(ArticleData.class).findAll();

            if(results.size() > 0) {
                this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        mList.setAdapter(new NewsAdapter(getActivity(), results));
                    }
                });
            }

            if(updated && success) {
                updater.setLastUpdated(UpdateContainer.UpdateTags.UPDATE_NEWS);
            }

            if(results.size() <= 0 || (!updated &&
                    updater.shouldUpdate(UpdateContainer.UpdateTags.UPDATE_NEWS, 45*60*1000) &&
                    updater.shouldUpdateViaTime(UpdateContainer.UpdateTags.UPDATE_NEWS, 15))) {
                onRefresh();
            }
        }
    }
}
