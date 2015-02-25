package cz.duong.wigym.data.students;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class StudentPageData extends RealmObject {
    private String name;
    private RealmList<StudentData> students;

    public RealmList<StudentData> getStudents() {
        return students;
    }

    public void setStudents(RealmList<StudentData> students) {
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
