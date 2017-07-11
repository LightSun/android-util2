package com.heaven7.android.util2;

import java.util.HashSet;
import java.util.Set;

/**
 * the lock helper help we handle mutex events.
 * Created by heaven7 on 2017/6/27 0027.
 */

public final class LockHelper {

    private Set<Integer> mEvents;

    /**
     * lock the target event
     * @param eventKey the event key
     * @return true if previous is not locked.
     */
    public boolean lockEvent(int eventKey) {
        if (mEvents == null) {
            mEvents = new HashSet<>(4);
        }
        return mEvents.add(eventKey);
    }

    /**
     * lock the event all success. or not.
     * @param events the events.
     * @return true if lock success.
     */
    public boolean lockEvent(int[] events) {
        if (mEvents == null) {
            mEvents = new HashSet<>(4);
        }
        boolean result = true;
        for(int event : events){
            result &= mEvents.add(event);
        }
        return result;
    }

    /**
     * unlock the event
     * @param eventKey the event key.
     * @return  true if unlock success.
     */
    public boolean unlockEvent(int eventKey) {
        if (mEvents == null) {
            return false;
        }
        return mEvents.remove(eventKey);
    }
    /**
     * unlock the target events
     * @param events the event keys.
     * @return  true if unlock the target events success.
     */
    public boolean unlockEvent(int[] events) {
        if (mEvents == null) {
            return false;
        }
        boolean result = true;
        for(int event : events){
            result &= mEvents.remove(event);
        }
        return result;
    }

    /**
     * unlock the all events.
     */
    public void unlockAllEvents(){
        if (mEvents != null) {
            mEvents.clear();
        }
    }

    /**
     * indicate the event is locked or not.
     * @param eventKey the event key
     * @return true if the event is locked.
     */
    public boolean isLockedEvent(int eventKey) {
        return mEvents != null && mEvents.contains(eventKey);
    }
}
