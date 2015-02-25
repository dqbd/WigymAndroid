package cz.duong.wigym.ui.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import cz.duong.wigym.R;
import cz.duong.wigym.Utils;
import cz.duong.wigym.data.teachers.TeacherAprobationData;
import cz.duong.wigym.data.teachers.TeacherHumanData;
import cz.duong.wigym.data.teachers.TeacherNameData;
import cz.duong.wigym.interfaces.TeacherListener;
import cz.duong.wigym.persistency.UpdateContainer;
import cz.duong.wigym.ui.adapters.TeachersAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Vytvořeno David on 24. 11. 2014.
 */
public class TeachersFragment extends Fragment implements TeacherListener, SwipeRefreshLayout.OnRefreshListener {
    private Realm database;
    private String url;

    private ListView list;
    private SwipeRefreshLayout refreshLayout;

    private UpdateContainer updater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View baseView = inflater.inflate(R.layout.teachers_fragment, container, false);

        url = getResources().getString(R.string.teachers_url);
        database = Realm.getInstance(this.getActivity());

        list = (ListView) baseView.findViewById(R.id.teachers_listview);
        refreshLayout = (SwipeRefreshLayout) baseView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.blue);
        refreshLayout.setOnRefreshListener(this);

        updater = new UpdateContainer(database);

        onTeacherDownload(false, false);

        return baseView;
    }

    @Override
    public void onTeacherDownload(boolean download, boolean success) {
        final RealmResults<TeacherHumanData> results = database.where(TeacherHumanData.class).findAll();

        if(results.size() > 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(list.getAdapter() == null) {
                        list.setAdapter(new TeachersAdapter(getActivity(), results));
                    } else {

                        ((TeachersAdapter) list.getAdapter()).updateRealmResults(results);
                    }
                }
            });
        }

        if(download && success) {
            updater.setLastUpdated(UpdateContainer.UpdateTags.UPDATE_TEACHERS);
            refreshLayout.setRefreshing(false);
        }

        if(results.size() <= 0 || (!download && updater.shouldUpdate(UpdateContainer.UpdateTags.UPDATE_TEACHERS, 30*DateUtils.DAY_IN_MILLIS))) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 1000);

        new TeacherDownloadTask(this).execute();
    }

    public class TeacherDownloadTask extends AsyncTask<Void, Void, Boolean> {

        private TeacherListener listener;
        private HashMap<String, List<String>> aprobations = new HashMap<String, List<String>>();
        private Collator collator;

        public TeacherDownloadTask(TeacherListener listener) {
            this.listener = listener;

            String[] shortcuts = getResources().getStringArray(R.array.teacher_subject_shortcuts);
            String[] names = getResources().getStringArray(R.array.teacher_subjects_names);

            for(int i = 0; i < names.length; i++) {
                List<String> shorts = new ArrayList<String>();
                Collections.addAll(shorts, shortcuts[i].split(","));

                aprobations.put(names[i], shorts);
            }

            collator = Collator.getInstance(new Locale("cs_CZ"));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", url);

                Elements el = doc.select(".entrybody table:eq(0) tr:gt(0)");

                database.beginTransaction();

                database.where(TeacherHumanData.class).findAll().clear();

                for(Element row : el) {
                    Elements cells = row.select("td");

                    TeacherHumanData teacher = database.createObject(TeacherHumanData.class);
                    generateName(teacher, cells.first().text());
                    generateAprobation(teacher, cells.get(1));

                    if(Utils.clearSpaces(cells.get(2).text()).length() > 0) {
                        teacher.setKeyNumber(Integer.parseInt(cells.get(2).text()));
                    }

                    teacher.setEmail(Utils.clearSpaces(cells.get(3).text()));
                    teacher.setConsultations(cells.get(4).text());
                }

                database.commitTransaction();

                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private void generateName(TeacherHumanData target, String text) {
            TeacherNameData data = database.createObject(TeacherNameData.class);

            data.setFullName(text);

            List<String> cleanName = new ArrayList<String>();
            for(String part : text.split(" ")) {
                if(!part.contains(".")) {
                    cleanName.add(part.replace(",", ""));
                }
            }

            data.setName(TextUtils.join(" ", cleanName));
            data.setFirstname(cleanName.get(cleanName.size() - 1));
            data.setLastname(TextUtils.join(" ", cleanName.subList(0, cleanName.size() - 1)));

            target.setName(data);
        }

        private void generateAprobation(TeacherHumanData target, Element item) {
            Collection<TeacherAprobationData> aprobationDataCollection = new ArrayList<TeacherAprobationData>();

            if(!item.text().equals(item.ownText())) {
                TeacherAprobationData additional = database.createObject(TeacherAprobationData.class);
                additional.setName(item.text().replace(item.ownText(), ""));

                aprobationDataCollection.add(additional);
            }

            for(String input : item.ownText().split(", ")) {
                input = Utils.clearSpaces(input);

                if(input.length() > 0) {

                    boolean has = false;
                    for(Map.Entry<String, List<String>> entry : aprobations.entrySet()) {

                        for(String comp : entry.getValue()) {
                            if(collator.equals(comp.toLowerCase(), input.toLowerCase())) {
                                has = true;

                                TeacherAprobationData aprobation = database.createObject(TeacherAprobationData.class);
                                aprobation.setKeyword(input);
                                aprobation.setName(entry.getKey());

                                aprobationDataCollection.add(aprobation);
                            }
                        }

                        if(has) {
                            break;
                        }
                    }

                    if(!has) {
                        TeacherAprobationData aprobation = database.createObject(TeacherAprobationData.class);
                        aprobation.setKeyword(input);
                        aprobation.setName(input);

                        aprobationDataCollection.add(aprobation);
                    }
                }

                target.getAprobations().addAll(aprobationDataCollection);

            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            listener.onTeacherDownload(true, aBoolean);
        }
    }
}
