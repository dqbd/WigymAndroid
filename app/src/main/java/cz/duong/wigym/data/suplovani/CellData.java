package cz.duong.wigym.data.suplovani;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 13. 11. 2014.
 */
public class CellData extends RealmObject {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
