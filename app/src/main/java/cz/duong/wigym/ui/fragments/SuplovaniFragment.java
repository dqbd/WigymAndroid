package cz.duong.wigym.ui.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.duong.wigym.R;
import cz.duong.wigym.Utils;
import cz.duong.wigym.persistency.UpdateContainer;
import cz.duong.wigym.ui.adapters.suplovani.SuplovaniAdapter;
import cz.duong.wigym.ui.adapters.suplovani.SuplovaniDayAdapter;
import cz.duong.wigym.data.suplovani.CellData;
import cz.duong.wigym.data.suplovani.ChangeData;
import cz.duong.wigym.data.suplovani.ClassData;
import cz.duong.wigym.data.suplovani.DayData;
import cz.duong.wigym.data.suplovani.DayInfoData;
import cz.duong.wigym.data.suplovani.HeadCellData;
import cz.duong.wigym.data.suplovani.RoomData;
import cz.duong.wigym.data.suplovani.TeacherData;
import cz.duong.wigym.interfaces.SuplovaniListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static cz.duong.wigym.Utils.dpToPx;

/**
 * Vytvořeno David on 11. 11. 2014.
 */
public class SuplovaniFragment extends Fragment implements SuplovaniListener, SwipeRefreshLayout.OnRefreshListener {

    private Realm database;
    private Spinner spinner;

    private ExpandableListView listview;
    private SwipeRefreshLayout refreshLayout;
    private String url;

    private UpdateContainer updater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.suplovani_fragment, container, false);

        database = Realm.getInstance(getActivity());
        spinner = (Spinner) baseView.findViewById(R.id.suplovani_days);
        listview = (ExpandableListView) baseView.findViewById(R.id.suplovani_list);

        url = getResources().getString(R.string.suplovani_url);

        refreshLayout = (SwipeRefreshLayout) baseView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.blue);
        refreshLayout.setOnRefreshListener(this);

        updater = new UpdateContainer(database);

        final SuplovaniFragment instance = this;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SuplovaniDayAdapter data = ((SuplovaniDayAdapter) spinner.getAdapter());
                listview.setAdapter(new SuplovaniAdapter(getActivity(), null));

                new PageTask(instance).execute(data.getItem(i).getDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        onRefresh();

        return baseView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        database.removeAllChangeListeners();
    }

    @Override
    public void onPageTaskLoaded(Date date) {
        if(listview != null && getActivity() != null) {
            listview.setAdapter(new SuplovaniAdapter(this.getActivity(), database.where(DayInfoData.class).equalTo("date", date).findFirst()));
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onListTaskLoaded(boolean completed) {
        final RealmResults<DayData> results = database.where(DayData.class).findAll();

        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(spinner != null) {
                        spinner.setAdapter(new SuplovaniDayAdapter(getActivity(), results));

                        animateSpinner();
                    }
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        new ListTask(this).execute();
    }

    public void animateSpinner() {
        final int negative = dpToPx(getActivity(), 64);
        if(spinner != null && ((LinearLayout.LayoutParams) spinner.getLayoutParams()).topMargin <= -negative) {
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) spinner.getLayoutParams();
                    params.topMargin = negative - (int)(negative / interpolatedTime);

                    spinner.setLayoutParams(params);
                }
            };

            a.setDuration(1000);
            a.setInterpolator(new DecelerateInterpolator());
            spinner.startAnimation(a);
        }
    }

    public class PageTask extends AsyncTask<Date, String, Date> {
        private SuplovaniListener listener;

        public PageTask(SuplovaniListener listener) {
            this.listener = listener;
        }

        @Override
        protected Date doInBackground(Date... date) {
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url + "tr" + new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(date[0]) + ".htm").openConnection();
                Document doc = Jsoup.parse(conn.getInputStream(), "windows-1250", url);


                database.beginTransaction();

                database.where(DayInfoData.class).equalTo("date", date[0]).findAll().clear();


                DayInfoData day = database.createObject(DayInfoData.class);

                day.setDate(date[0]);

                //get head cell
                HeadCellData headcell;
                if(doc.select(".tb_abucit_1").size() > 0) {
                    Elements cell = doc.select(".tb_abucit_1 tr:eq(0) .td_abucit_1 + .td_abucit_1");

                    headcell = database.createObject(HeadCellData.class);
                    headcell.setBegin(Integer.parseInt(cell.first().text().replace(".", "")));
                    headcell.setEnd(Integer.parseInt(cell.last().text().replace(".", "")));
                } else if (doc.select(".tb_abmist_1").size() > 0) {
                    Elements cell = doc.select(".tb_abmist_1 tr:eq(0) .td_abmist_1 + .td_abmist_1");

                    headcell = database.createObject(HeadCellData.class);
                    headcell.setBegin(Integer.parseInt(cell.first().text().replace(".", "")));
                    headcell.setEnd(Integer.parseInt(cell.last().text().replace(".", "")));
                } else {
                    headcell = database.createObject(HeadCellData.class);
                    headcell.setBegin(0);
                    headcell.setEnd(9);
                }

                day.setHeaders(headcell);

                //teachers
                for(Element rows : doc.select(".tb_abucit_1 tr + tr")) {
                    Elements cells = rows.select("td");

                    TeacherData teacher = database.createObject(TeacherData.class);
                    teacher.setName(cells.get(0).text());

                    for(int i = 1; i < cells.size(); i++) {
                        CellData data = database.createObject(CellData.class);

                        data.setContent(cells.get(i).text());

                        teacher.getLessons().add(data);
                    }

                    day.getTeachers().add(teacher);
                }

                //rooms
                for(Element rows : doc.select(".tb_abmist_1 tr + tr")) {
                    Elements cells = rows.select("td");

                    RoomData room = database.createObject(RoomData.class);
                    room.setName(cells.get(0).text());

                    for(int i = 1; i < cells.size(); i++) {
                        CellData data = database.createObject(CellData.class);

                        data.setContent(cells.get(i).text());

                        room.getLessons().add(data);
                    }

                    day.getRooms().add(room);
                }

                ArrayList<ClassData> classes = new ArrayList<ClassData>();
                Elements rows = doc.select(".tb_supltrid_1 tr:not(.tr_supltrid_1)");

                for(Element row : rows) {
                    Elements cells = row.select("td");
                    String name = Utils.clearSpaces(cells.first().text());

                    if(name.length() > 0) {
                        if(classes.size() > 0) {
                            day.getClasses().add(classes.get(classes.size() - 1));
                        }

                        ClassData classData = database.createObject(ClassData.class);
                        classData.setName(name);

                        classes.add(classData);
                    }

                    ClassData classData = classes.get(classes.size() - 1);
                    ChangeData changes = database.createObject(ChangeData.class);

                    if(cells.size() > 2) {
                        changes.setTime(cells.get(1).text());
                        changes.setLesson(cells.get(2).text());
                        changes.setGroup(cells.get(3).text());
                        changes.setRoom(cells.get(4).text());
                        changes.setChange(cells.get(5).text());
                        changes.setDesc(cells.get(6).text());
                        changes.setDetail(cells.get(7).text());

                        changes.setCondensed(false);
                    } else {
                        changes.setChange(cells.get(1).text());
                        changes.setCondensed(true);
                    }

                    classData.getChanges().add(changes);

                    if(row.equals(rows.last())) {
                        day.getClasses().add(classData);
                    }
                }

                database.commitTransaction();

                return date[0];
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Date date) {
            listener.onPageTaskLoaded(date);
        }
    }

    public class ListTask extends AsyncTask<Void, String, Boolean> {
        private SuplovaniListener listener;

        public ListTask(SuplovaniListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... strings) {
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url + "sutrhlav.htm").openConnection();

                Elements elements = Jsoup.parse(conn.getInputStream(), "windows-1250", url)
                                         .select("select > option");

                SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());

                database.beginTransaction();

                if(elements.size() > 0) {
                    database.where(DayData.class).findAll().clear();
                }

                for(Element e : elements) {
                    DayData item = database.createObject(DayData.class);
                    item.setDate(format.parse(e.attr("value"), new ParsePosition(2)));
                    item.setLink(url + e.attr("value"));
                }

                database.commitTransaction();

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(conn != null) {
                    conn.disconnect();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            listener.onListTaskLoaded(aBoolean);
        }
    }


}
