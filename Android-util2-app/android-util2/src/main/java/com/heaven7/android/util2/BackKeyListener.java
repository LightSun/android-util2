package com.heaven7.android.util2;

import android.view.KeyEvent;
import android.view.View;

/**
 *
 * Created by heaven7 on 2017/8/16 0016.
 * @since 1.0.7
 */

public abstract class BackKeyListener implements View.OnKeyListener{

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            onBackPressed();
            return true;
        }
        return false;
    }

    protected abstract void onBackPressed();

}
