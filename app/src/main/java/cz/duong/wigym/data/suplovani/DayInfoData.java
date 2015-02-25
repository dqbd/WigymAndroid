package cz.duong.wigym.data.suplovani;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Vytvořeno David on 10. 11. 2014.
 */
public class DayInfoData extends RealmObject {
    private RealmList<TeacherData> teachers;
    private RealmList<RoomData> rooms;
    private RealmList<ClassData> classes;

    private Date date;
    private HeadCellData headers;

    public RealmList<TeacherData> getTeachers() {
        return teachers;
    }
    public void setTeachers(RealmList<TeacherData> teachers) {
        this.teachers = teachers;
    }

    public RealmList<RoomData> getRooms() {
        return rooms;
    }

    public void setRooms(RealmList<RoomData> rooms) {
        this.rooms = rooms;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<ClassData> getClasses() {
        return classes;
    }

    public void setClasses(RealmList<ClassData> classes) {
        this.classes = classes;
    }

    public HeadCellData getHeaders() {
        return headers;
    }

    public void setHeaders(HeadCellData headers) {
        this.headers = headers;
    }
}
