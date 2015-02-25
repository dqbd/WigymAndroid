package cz.duong.wigym.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import cz.duong.wigym.R;
import cz.duong.wigym.data.teachers.TeacherHumanData;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Vytvořeno David on 2. 12. 2014.
 */
public class TeachersAdapter extends RealmBaseAdapter<TeacherHumanData> implements SectionIndexer {

    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;

    private class TeacherHolder {
        TextView name;
        TextView letter;
    }

    public TeachersAdapter(Context context, RealmResults<TeacherHumanData> realmResults) {
        super(context, realmResults, false);

        Collator collator = Collator.getInstance(new Locale("cs_CZ"));

        alphaIndexer = new HashMap<String, Integer>();

        for(int i = 0; i < realmResults.size(); i++) {
            String letter = realmResults.get(i).getName().getName().substring(0, 1).toUpperCase();
            if(!alphaIndexer.containsKey(letter)) {
                alphaIndexer.put(letter, i);
            }
        }
        ArrayList<String> sectionList = new ArrayList<String>(alphaIndexer.keySet());
        Collections.sort(sectionList, collator);

        sections = new String[sectionList.size()];
        for(int i = 0; i < sectionList.size(); i++) {
            sections[i] = sectionList.get(i);
        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int i) {

        return alphaIndexer.get(sections[i]);
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = sections.length - 1; i >= 0; i--) {
            if (position >= alphaIndexer.get(sections[i])) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return alphaIndexer.containsValue(position) ? 1 : 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TeacherHolder holder;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.teacher_listitem, viewGroup, false);

            holder = new TeacherHolder();
            holder.name = (TextView) view.findViewById(R.id.teacher_name);
            holder.letter = (TextView) view.findViewById(R.id.teacher_name_capital);
            view.setTag(holder);
        } else {
            holder = (TeacherHolder) view.getTag();
        }


        TeacherHumanData item = getItem(i);

        holder.name.setText(item.getName().getName());

        if(getItemViewType(i) == 1) {
            holder.letter.setText(item.getName().getName().substring(0, 1).toUpperCase());
            holder.letter.setVisibility(View.VISIBLE);
        } else {
            holder.letter.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
