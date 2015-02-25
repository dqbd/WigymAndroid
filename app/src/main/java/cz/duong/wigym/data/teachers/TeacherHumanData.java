package cz.duong.wigym.data.teachers;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Vytvořeno David on 27. 11. 2014.
 */
public class TeacherHumanData extends RealmObject {
    private TeacherNameData name;
    private RealmList<TeacherAprobationData> aprobations;
    private int keyNumber;
    private String email;
    private String consultations;


    public int getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(int keyNumber) {
        this.keyNumber = keyNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConsultations() {
        return consultations;
    }

    public void setConsultations(String consultations) {
        this.consultations = consultations;
    }

    public RealmList<TeacherAprobationData> getAprobations() {
        return aprobations;
    }

    public void setAprobations(RealmList<TeacherAprobationData> aprobations) {
        this.aprobations = aprobations;
    }

    public TeacherNameData getName() {
        return name;
    }

    public void setName(TeacherNameData name) {
        this.name = name;
    }
}

