package cz.duong.wigym.ui.adapters.students;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.duong.wigym.R;
import cz.duong.wigym.data.students.StudentPageData;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class StudentPageAdapter extends RealmBaseAdapter<StudentPageData> {

    private static class PageHolder {
        TextView text;
    }

    public StudentPageAdapter(Context context, RealmResults<StudentPageData> realmResults) {
        super(context, realmResults, false);
    }

    public View setupView(int position, View convertView, ViewGroup parent, int layout) {
        PageHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);

            holder = new PageHolder();
            holder.text = (TextView) convertView.findViewById(R.id.students_page);

            convertView.setTag(holder);
        } else {
            holder = (PageHolder) convertView.getTag();
        }

        holder.text.setText(getItem(position).getName());

        return convertView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return setupView(position, convertView, parent, R.layout.student_pagelabel_selected);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return setupView(position, convertView, parent, R.layout.student_pagelabel);
    }


}
