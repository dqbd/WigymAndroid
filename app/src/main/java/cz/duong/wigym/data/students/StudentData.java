package cz.duong.wigym.data.students;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 22. 11. 2014.
 */
public class StudentData extends RealmObject {
    private String firstname;
    private String lastname;
    private int group;
    private int secondLang;
    private int gender;

    private boolean tick;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getSecondLang() {
        return secondLang;
    }

    public void setSecondLang(int second_lang) {
        this.secondLang = second_lang;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isTick() {
        return tick;
    }

    public void setTick(boolean tick) {
        this.tick = tick;
    }
}
