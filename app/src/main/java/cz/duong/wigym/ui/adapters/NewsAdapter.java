package cz.duong.wigym.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cz.duong.wigym.R;
import cz.duong.wigym.data.ArticleData;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import static cz.duong.wigym.Utils.dpToPx;

public class NewsAdapter extends RealmBaseAdapter<ArticleData> implements ListAdapter {
    private static class ArticleViewHolder {
        TextView title;
        TextView detail;

        ImageView image;
        TextView imagecaps;
    }

    public NewsAdapter(Context context, RealmResults<ArticleData> realmResults) {
        super(context, realmResults, false);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ArticleViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_listitem, viewGroup, false);

            viewHolder = new ArticleViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.news_item_title);
            viewHolder.detail = (TextView) convertView.findViewById(R.id.news_item_detail);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.news_item_image);
            viewHolder.imagecaps = (TextView) convertView.findViewById(R.id.news_item_image_caps);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ArticleViewHolder) convertView.getTag();
        }


        ArticleData item = realmResults.get(i);

        if(item.getImage().length() > 0) { //+1dp pro hezky vysledek
            Picasso.with(context).load(item.getImage()).resize(dpToPx(context, 41), dpToPx(context, 41)).centerCrop().into(viewHolder.image);
        } else {
            viewHolder.image.setImageResource(android.R.color.transparent);
        }

        viewHolder.imagecaps.setText(String.valueOf(item.getAuthor().charAt(0)));

        viewHolder.title.setText(item.getTitle());
        viewHolder.detail.setText(item.getAuthor());

        return convertView;
    }


}
