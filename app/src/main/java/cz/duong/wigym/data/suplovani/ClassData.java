package cz.duong.wigym.data.suplovani;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Vytvořeno David on 13. 11. 2014.
 */
public class ClassData extends RealmObject {
    private String name;
    private RealmList<ChangeData> changes;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<ChangeData> getChanges() {
        return changes;
    }

    public void setChanges(RealmList<ChangeData> changes) {
        this.changes = changes;
    }
}
