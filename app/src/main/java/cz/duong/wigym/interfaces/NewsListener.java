package cz.duong.wigym.interfaces;

import com.pkmmte.pkrss.Callback;

/**
 * Vytvořeno David on 24. 11. 2014.
 */
public interface NewsListener extends Callback {
    public void onDataLoaded(boolean updated, boolean success);
}
