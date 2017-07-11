package com.heaven7.android.util2.logic;

/**
 * Created by heaven7 on 2017/6/27 0027.
 */

public abstract class LogicRunner {

    public abstract void run(LogicAction action, int tag, int count, LogicParam param);

    public void cancel(int tag, boolean immediately){

    }

}
