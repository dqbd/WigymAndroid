package cz.duong.wigym.persistency;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.duong.wigym.data.UpdateData;
import io.realm.Realm;

/**
 * Vytvořeno David on 3. 12. 2014.
 */
public class UpdateContainer {
    private Realm database;

    public static enum UpdateTags { UPDATE_NEWS, UPDATE_TEACHERS, UPDATE_SUPLOVANI }

    public UpdateContainer(Realm database) {
        this.database = database;
    }

    public boolean shouldUpdate(UpdateTags tag, long delay_inmilis) {
        Date date = getLastUpdated(tag);
        return date == null || (new Date().getTime() - date.getTime()) > delay_inmilis;
    }

    public boolean shouldUpdateViaTime(UpdateTags tag, int hour) {
        Date date = getLastUpdated(tag);
        if(date != null) {
            Calendar cal = Calendar.getInstance(new Locale("cz_CZ"));
            cal.setTime(date);
            return hour <= cal.get(Calendar.HOUR_OF_DAY);
        }

        return true;
    }

    public Date getLastUpdated(UpdateTags tag) {
        if(database != null) {

            UpdateData data = database.where(UpdateData.class).findFirst();

            if(data == null) { return null; }

            if(tag == UpdateTags.UPDATE_NEWS) {
                return data.getNews();
            } else if (tag == UpdateTags.UPDATE_SUPLOVANI) {
                return data.getSuplovani();
            } else if (tag == UpdateTags.UPDATE_TEACHERS) {
                return data.getTeachers();
            }
        }

        return null;
    }

    public void setLastUpdated(UpdateTags tag) {
        setLastUpdated(tag, new Date());
    }

    public void setLastUpdated(UpdateTags tag, Date date) {
        if(database != null) {

            database.beginTransaction();
            UpdateData data = (database.where(UpdateData.class).count() > 0) ?
                    database.where(UpdateData.class).findFirst() :
                    database.createObject(UpdateData.class);

            if(tag == UpdateTags.UPDATE_NEWS) {
                data.setNews(date);
            } else if (tag == UpdateTags.UPDATE_SUPLOVANI) {
                data.setSuplovani(date);
            } else if (tag == UpdateTags.UPDATE_TEACHERS) {
                data.setTeachers(date);
            }

            database.commitTransaction();

        }
    }
}
