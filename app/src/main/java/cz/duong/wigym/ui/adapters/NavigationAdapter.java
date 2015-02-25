package cz.duong.wigym.ui.adapters;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cz.duong.wigym.Application;
import cz.duong.wigym.R;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class NavigationAdapter extends BaseAdapter {

    private class NavigationHolder {
        TextView label;
        ImageView icon;
    }

    private List<Application.FragmentContainer.FragmentInfo> fragments;
    private Context context;
    public NavigationAdapter(Context context, List<Application.FragmentContainer.FragmentInfo> fragments) {
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Application.FragmentContainer.FragmentInfo getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NavigationHolder holder;
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.navigation_drawer_item, viewGroup, false);

            holder = new NavigationHolder();
            holder.label = (TextView) view.findViewById(R.id.navigation_drawer_label);
            holder.icon = (ImageView) view.findViewById(R.id.navigation_drawer_icon);
            view.setTag(holder);
        } else {
            holder = (NavigationHolder) view.getTag();
        }

        Application.FragmentContainer.FragmentInfo info = getItem(i);
        holder.label.setText(info.getName(context));
        holder.icon.setImageResource(info.getImage());

        return view;
    }
}
