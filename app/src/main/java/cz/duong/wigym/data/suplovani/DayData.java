package cz.duong.wigym.data.suplovani;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 12. 11. 2014.
 */
public class DayData extends RealmObject {
    private Date date;
    private String link;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
