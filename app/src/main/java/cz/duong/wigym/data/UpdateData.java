package cz.duong.wigym.data;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 2. 12. 2014.
 */
public class UpdateData extends RealmObject {
    private Date teachers;
    private Date suplovani;
    private Date news;

    public Date getTeachers() {
        return teachers;
    }

    public void setTeachers(Date teachers) {
        this.teachers = teachers;
    }

    public Date getSuplovani() {
        return suplovani;
    }

    public void setSuplovani(Date suplovani) {
        this.suplovani = suplovani;
    }

    public Date getNews() {
        return news;
    }

    public void setNews(Date news) {
        this.news = news;
    }
}
