package cz.duong.wigym.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import cz.duong.wigym.R;
import cz.duong.wigym.ui.activities.MainActivity;
import cz.duong.wigym.ui.adapters.students.StudentPageAdapter;
import cz.duong.wigym.ui.adapters.students.StudentsAdapter;
import cz.duong.wigym.data.students.StudentData;
import cz.duong.wigym.data.students.StudentPageData;
import io.realm.Realm;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class StudentsFragment extends Fragment {

    private Realm db;

    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.students_fragment, container, false);

        Spinner spinner = (Spinner) baseView.findViewById(R.id.students_pages);
        list = (ListView) baseView.findViewById(R.id.students_list);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StudentPageAdapter adapter = ((StudentPageAdapter) adapterView.getAdapter());
                list.setAdapter(new StudentsAdapter(getActivity(), adapter.getItem(i).getStudents()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StudentsAdapter adapter = ((StudentsAdapter) adapterView.getAdapter());
                adapter.setTick(db, i);
            }
        });

        db = Realm.getInstance(this.getActivity());

        if(db.where(StudentPageData.class).count() <= 0) {
            generatePage(getString(R.string.student_page_name_entire), null, null, null);
            generatePage(getString(R.string.student_page_name_firstgroup), 1, null, null);
            generatePage(getString(R.string.student_page_name_secondgroup), 2, null, null);
            generatePage(getString(R.string.student_page_name_german), null, 1, null);
            generatePage(getString(R.string.student_page_name_spanish), null, 2, null);
            generatePage(getString(R.string.student_page_name_boys), null, null, 0);
            generatePage(getString(R.string.student_page_name_girls), null, null, 1);
        }

        spinner.setAdapter(new StudentPageAdapter(this.getActivity(), db.where(StudentPageData.class).findAll()));

        return baseView;
    }



    private void generatePage(String name, Integer group, Integer language, Integer gender) {
        final String[][] predefined = {
            {"Tat Dat", "Duong", "1", "1", "0"},
            {"Michaela", "Farna", "1", "2", "1"},
            {"Vojta", "Jarema", "2", "2", "0"},
            {"Filip", "Ječmínek", "1", "1", "0"},
            {"Martin", "Jílka", "2", "2", "0"},
            {"Darina", "Kmentová", "2", "1", "1"},
            {"Jakub", "Kočur", "2", "1", "0"},
            {"Karolína", "Kovaříková", "2", "1", "1"},
            {"Jakub", "Kožušník", "1", "1", "0"},
            {"Vojta", "Maar", "1", "2", "0"},
            {"Markéta", "Machalová", "2", "2", "1"},
            {"Honza", "Martinásek", "2", "2", "0"},
            {"Anička", "Matějová", "2", "2", "1"},
            {"Monika", "Motanová", "1", "1", "1"},
            {"Kateřina", "Najsrová", "2", "2", "1"},
            {"Adam", "Neuwirth", "2", "1", "0"},
            {"Miroslava", "Nováková", "2", "2", "1"},
            {"Ondřej", "Pelíšek", "1", "2", "0"},
            {"David", "Pinkas", "1", "1", "0"},
            {"Nela", "Skowronková", "2", "1", "1"},
            {"Kateřina", "Suderová", "1", "1", "1"},
            {"Ondřej", "Sucháček", "1", "2", "0"},
            {"Petr", "Šindlář", "1", "2", "0"},
            {"Petr", "Šuchma", "1", "2", "0"},
            {"Markéta", "Švidrnochová", "2", "1", "1"},
            {"Matyáš", "Tichý", "1", "1", "0"},
            {"Veronika", "Trawinská", "1", "2", "1"},
            {"Anna", "Závodná", "2", "2", "1"}
        };

        db.beginTransaction();

        StudentPageData page = db.createObject(StudentPageData.class);
        page.setName(name);

        for(String[] personData : predefined) {

            if((group != null && (Integer.valueOf(personData[2]).equals(group)) || group == null) &&
            (language != null && (Integer.valueOf(personData[3]).equals(language)) || language == null) &&
            (gender != null && (Integer.valueOf(personData[4]).equals(gender)) || gender == null)) {

                StudentData person = db.createObject(StudentData.class);
                person.setFirstname(personData[0]);
                person.setLastname(personData[1]);
                person.setGroup(Integer.valueOf(personData[2]));
                person.setSecondLang(Integer.valueOf(personData[3]));
                person.setGender(Integer.valueOf(personData[3]));

                page.getStudents().add(person);
            }
        }

        db.commitTransaction();

    }
}
