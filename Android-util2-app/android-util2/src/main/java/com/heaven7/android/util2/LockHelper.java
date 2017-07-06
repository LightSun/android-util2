package com.heaven7.android.util2;

import java.util.ArrayList;

/**
 * Created by heaven7 on 2017/6/27 0027.
 */

public final class LockHelper {

    private ArrayList<Integer> mLockList;

    public boolean lockEvent(int eventKey) {
        if (mLockList == null) {
            mLockList = new ArrayList<>(4);
        }
        if (mLockList.contains(eventKey)) {
            return false;
        }
        mLockList.add(eventKey);
        return true;
    }

    public boolean unlockEvent(int eventKey) {
        if (mLockList == null) {
            return false;
        }
        int index = mLockList.indexOf(eventKey);
        if (index >= 0) {
            mLockList.remove(index);
            return true;
        }
        return false;
    }

    public boolean isLockedEvent(int eventKey) {
        return mLockList != null && mLockList.contains(eventKey);
    }
}
