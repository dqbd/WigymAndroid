package cz.duong.wigym.data.teachers;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 3. 12. 2014.
 */
public class TeacherNameData extends RealmObject {
    private String fullName;
    private String name;

    private String lastname;
    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
