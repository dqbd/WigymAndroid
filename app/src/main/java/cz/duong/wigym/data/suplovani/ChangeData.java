package cz.duong.wigym.data.suplovani;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 10. 11. 2014.
 */
public class ChangeData extends RealmObject {
    private String time;
    private String lesson;
    private String group;
    private String room;
    private String change;
    private String desc;
    private String detail;

    private boolean condensed;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isCondensed() {
        return condensed;
    }

    public void setCondensed(boolean condensed) {
        this.condensed = condensed;
    }
}
