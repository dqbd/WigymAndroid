package cz.duong.wigym.ui.adapters.suplovani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cz.duong.wigym.R;
import cz.duong.wigym.data.suplovani.CellData;
import cz.duong.wigym.data.suplovani.HeadCellData;
import io.realm.RealmList;

/**
 * Vytvořeno David on 16. 11. 2014.
 */
public class SuplovaniTableAdapter extends BaseAdapter {
    HeadCellData mHeader;
    RealmList<CellData> mCells;
    Context mContext;

    private class CellHolder {
        TextView label;
        TextView content;
    }

    public SuplovaniTableAdapter(Context context, RealmList<CellData> cells, HeadCellData header) {
        mContext = context;
        mCells = cells;
        mHeader = header;
    }

    @Override
    public int getCount() {
        return mCells.size();
    }

    @Override
    public CellData getItem(int i) {
        return mCells.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        CellHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_table_cell, viewGroup, false);

            holder = new CellHolder();
            holder.label = (TextView) convertView.findViewById(R.id.suplovani_cell_label);
            holder.content = (TextView) convertView.findViewById(R.id.suplovani_cell_content);

            convertView.setTag(holder);
        } else {
            holder = (CellHolder) convertView.getTag();
        }

        CellData item = getItem(i);

        holder.label.setText(String.valueOf(mHeader.getBegin() + i));
        holder.content.setText(item.getContent());

        return convertView;
    }
}
