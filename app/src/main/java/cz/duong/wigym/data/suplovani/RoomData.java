package cz.duong.wigym.data.suplovani;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Vytvořeno David on 13. 11. 2014.
 */
public class RoomData extends RealmObject {
    private String name;
    private RealmList<CellData> lessons;

    public RealmList<CellData> getLessons() {
        return lessons;
    }

    public void setLessons(RealmList<CellData> lessons) {
        this.lessons = lessons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
