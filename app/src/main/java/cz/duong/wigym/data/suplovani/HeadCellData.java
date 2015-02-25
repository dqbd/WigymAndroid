package cz.duong.wigym.data.suplovani;

import io.realm.RealmObject;

/**
 * Vytvořeno David on 16. 11. 2014.
 */
public class HeadCellData extends RealmObject {
    private int begin;
    private int end;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
