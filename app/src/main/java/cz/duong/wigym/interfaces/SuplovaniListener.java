package cz.duong.wigym.interfaces;

import java.util.Date;

/**
 * Vytvořeno David on 13. 11. 2014.
 */
public interface SuplovaniListener {
    public void onPageTaskLoaded(Date date);
    public void onListTaskLoaded(boolean completed);
}
