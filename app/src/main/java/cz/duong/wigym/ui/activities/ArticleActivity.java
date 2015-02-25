package cz.duong.wigym.ui.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cz.duong.wigym.R;
import cz.duong.wigym.data.ArticleData;
import io.realm.Realm;

import static cz.duong.wigym.Utils.dpToPx;


public class ArticleActivity extends ActionBarActivity {

    public static String ARTICLE_ID = "ARTICLE_ID";

    private Realm database;
    private ScrollView scrollView;

    private Toolbar toolbar;

    private ImageView imageView;

    private ArticleData article;
    private LinearLayout card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        database = Realm.getInstance(this);
        article = database.where(ArticleData.class).equalTo("id", getIntent().getIntExtra(ARTICLE_ID, -1)).findFirst();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setTitle(article.getTitle());

        setSupportActionBar(toolbar);

        scrollView = (ScrollView) findViewById(R.id.news_article_scrollview);
        imageView = (ImageView) findViewById(R.id.news_article_coverimage);
        card = (LinearLayout) findViewById(R.id.news_article_card);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(article != null && article.getImage().length() > 0) {
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();

                    params.height = Math.max(dpToPx(getBaseContext(), 150) - scrollView.getScrollY(), 0);
                    imageView.setLayoutParams(params);

                    LinearLayout.LayoutParams card_params = (LinearLayout.LayoutParams) card.getLayoutParams();
                    card_params.topMargin = -(dpToPx(getBaseContext(), 150) - params.height);

                    card.setLayoutParams(card_params);


                    double opacity = 1.0 - Math.max((params.height + card_params.topMargin) * 1.0 / dpToPx(getBaseContext(), 150), 0);

                    toolbar.getBackground().setAlpha((int) (255 * opacity));
                    toolbar.setTitleTextColor(Color.argb((int) (255 * opacity), 255, 255, 255));


                }
            }
        });

        Document doc = Jsoup.parse(article.getContent());
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        doc = cleaner.clean(doc);

        for (Element element : doc.select("*")) {
            String text = element.text();
            text = text.replaceAll("\\s+", "").replace("&nbsp;", "");

            if (text.length() <= 0) {
                element.remove();
            }
        }

        if(article.getImage().length() > 0) {
            Picasso.with(this)
                    .load(article.getImage())
                    .resize(getResources().getDisplayMetrics().widthPixels, dpToPx(this, 150))
                    .centerCrop()
                    .into(imageView);


            toolbar.getBackground().setAlpha(0);


        } else {
            ((FrameLayout)imageView.getParent()).getLayoutParams().height = dpToPx(this, 56);
        }


        toolbar.setTitleTextColor(Color.argb(0, 255, 255, 255));

        ((TextView) findViewById(R.id.news_article_title)).setText(article.getTitle());
        ((TextView) findViewById(R.id.news_article_content)).setText(Html.fromHtml(doc.html()));
    }

    private ArrayList<Uri> findImages(Document doc) {
        ArrayList<Uri> result = new ArrayList<Uri>();

        Elements links = doc.select("a[href~=\\.(png|jpe?g|gif)$]");
        Elements images = doc.select("img[src~=\\.(png|jpe?g|gif)$]:not(.wp-smiley)");

        for(Element e : links) {
            result.add(Uri.parse(e.attr("href")));
        }

        for(Element img : images) {
            Uri url = Uri.parse(img.attr("href"));

            String path = url.getLastPathSegment().split("\\.")[0];

            if(path != null) {
                boolean added = false;
                for(Uri check : result) {
                    if(check.getLastPathSegment().contains(path)) {
                        added = true;
                        break;
                    }
                }

                if(!added) {
                    result.add(url);
                }
            }
        }

        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
