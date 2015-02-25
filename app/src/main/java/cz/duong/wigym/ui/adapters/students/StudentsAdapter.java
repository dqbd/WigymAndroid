package cz.duong.wigym.ui.adapters.students;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import cz.duong.wigym.R;
import cz.duong.wigym.data.students.StudentData;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class StudentsAdapter extends BaseAdapter {

    private class StudentHolder {
        ImageView status;
        TextView name;
        TextView position;
    }

    private Context context;
    private RealmList<StudentData> data;

    private int currentDay;

    public StudentsAdapter(Context context, RealmList<StudentData> data) {
        this.context = context;
        this.data = data;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        currentDay = (cal.get(Calendar.DAY_OF_MONTH) % data.size()) - 1;
        currentDay = (currentDay == -1) ? (data.size() - 1) : currentDay;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public StudentData getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        StudentHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_listitem, viewGroup, false);

            holder = new StudentHolder();
            holder.name = (TextView) convertView.findViewById(R.id.student_name);
            holder.status = (ImageView) convertView.findViewById(R.id.student_status);
            holder.position = (TextView) convertView.findViewById(R.id.student_position);
            convertView.setTag(holder);
        } else {
            holder = (StudentHolder) convertView.getTag();
        }

        StudentData data = getItem(position);


        Drawable tick = context.getResources().getDrawable(R.drawable.ic_done_white_24dp);
        if(!data.isTick() && position == currentDay) {
            tick = context.getResources().getDrawable(R.drawable.ic_assignment_late_white_24dp);
            tick.mutate().setColorFilter(context.getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
        } else {
            if (position == currentDay) {
                tick = context.getResources().getDrawable(R.drawable.ic_assignment_turned_in_white_24dp);
            }
            tick.mutate().setColorFilter(context.getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
        }

        holder.name.setText(data.getFirstname() + " " + data.getLastname());
        holder.status.setImageDrawable(tick);
        holder.status.setVisibility((data.isTick() || position == currentDay) ? View.VISIBLE : View.INVISIBLE);

        holder.position.setText(String.format("%02d",position+1) + ".");

        return convertView;
    }

    public void setTick(Realm db, int position) {
        db.beginTransaction();
        getItem(position).setTick((!getItem(position).isTick()));
        db.commitTransaction();

        notifyDataSetChanged();
    }
}
