package cz.duong.wigym.ui.adapters.suplovani;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import cz.duong.wigym.R;
import cz.duong.wigym.data.suplovani.DayData;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Vytvořeno David on 12. 11. 2014.
 */
public class SuplovaniDayAdapter extends RealmBaseAdapter<DayData> implements ListAdapter {

    private long today;
    private List<String> labels = new ArrayList<String>();
    private static class DayHolder {
        TextView label;
    }

    public SuplovaniDayAdapter(Context context, RealmResults<DayData> realmResults) {
        super(context, realmResults, false);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        today = cal.getTimeInMillis();


        for(int i = 0; i < realmResults.size(); i++) {
            long time = realmResults.get(i).getDate().getTime();
            String[] strings = context.getResources().getStringArray(R.array.suplovani_list_relative);


            int days = Math.round((time - today) / DateUtils.DAY_IN_MILLIS);

            if(Math.abs(days) <= 4) {
                int id = Math.max(Math.min(5 + days, strings.length - 1), 0);

                labels.add(i, String.format(strings[id], Math.abs(days)));
            } else {
                labels.add(i, (String) DateFormat.format(context.getResources().getString(R.string.suplovani_list_many), time));
            }

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DayHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.suplovani_dayitem_selected, parent, false);

            holder = new DayHolder();
            holder.label = (TextView) convertView.findViewById(R.id.suplovani_dayitem);
            convertView.setTag(holder);
        } else {
            holder = (DayHolder) convertView.getTag();
        }

        setupView(position, holder);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        DayHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.suplovani_dayitem, parent, false);

            holder = new DayHolder();
            holder.label = (TextView) convertView.findViewById(R.id.suplovani_dayitem);
            convertView.setTag(holder);
        } else {
            holder = (DayHolder) convertView.getTag();
        }

        setupView(position, holder);

        return convertView;
    }

    private void setupView(int position, DayHolder holder) {
        DayData data = getItem(position);

        holder.label.setText(labels.get(position));
    }
}
