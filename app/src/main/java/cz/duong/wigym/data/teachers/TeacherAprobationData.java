package cz.duong.wigym.data.teachers;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 27. 11. 2014.
 */
public class TeacherAprobationData extends RealmObject {
    private String keyword;
    private String name;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
