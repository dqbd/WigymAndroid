package cz.duong.wigym;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import cz.duong.wigym.ui.fragments.NewsFragment;
import cz.duong.wigym.ui.fragments.StudentsFragment;
import cz.duong.wigym.ui.fragments.SuplovaniFragment;
import cz.duong.wigym.ui.fragments.TeachersFragment;
import io.realm.Realm;

/**
 * Vytvořeno David on 11. 11. 2014.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Realm.getInstance(getBaseContext());
        } catch(Exception e) {
            Log.e("WIGYM-ERROR", Log.getStackTraceString(e));
            clearEverything();
        }
    }

    public void clearEverything() {
        File f = getFilesDir();

        for(File file : f.listFiles()) {
            if(!file.delete()) {
                Log.d("WIGYM-ERROR-DELETE", file.getName());
            }
        }
    }

    public static class FragmentContainer {
        public static class FragmentInfo {
            private Class<? extends Fragment> target;
            private int name;
            private int image;

            public FragmentInfo(int name, int image, Class<? extends Fragment> target) {
                this.name = name;
                this.image = image;
                this.target = target;
            }

            public int getIdName() {
                return this.name;
            }
            public CharSequence getName(Context context) {
                return context.getText(name);
            }

            public Fragment getFragment() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
                return target.getConstructor().newInstance();
            }

            public int getImage() {
                return image;
            }
        }

        private static final List<FragmentInfo> mList = new ArrayList<FragmentInfo>() {{
            add(new FragmentInfo(R.string.fragment_mews, R.drawable.ic_news, NewsFragment.class));
            add(new FragmentInfo(R.string.fragment_suplovani, R.drawable.ic_suplovani, SuplovaniFragment.class));
            add(new FragmentInfo(R.string.fragment_teachers, R.drawable.ic_teachers, TeachersFragment.class));
            add(new FragmentInfo(R.string.fragment_students, R.drawable.ic_students, StudentsFragment.class));
        }};

        public List<FragmentInfo> getList() {
            return mList;
        }

        public String[] getNames(Context context) {
            ArrayList<String> names = new ArrayList<String>();

            for(FragmentInfo info : mList) {
                names.add((String) info.getName(context));
            }

            return names.toArray(new String[mList.size()]);
        }

        public FragmentInfo getFragment(int id) {
            if(id >= mList.size()) {
                throw new ArrayIndexOutOfBoundsException();
            }

            return mList.get(id);
        }
    }

}
