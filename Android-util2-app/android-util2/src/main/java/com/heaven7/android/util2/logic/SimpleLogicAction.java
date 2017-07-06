package com.heaven7.android.util2.logic;

import com.heaven7.java.base.util.Throwables;

/**
 * Created by heaven7 on 2017/6/27 0027.
 */

public class SimpleLogicAction extends AbstractLogicAction{

    private final LogicRunner executor;

    public SimpleLogicAction(LogicRunner executor) {
        super(false);
        Throwables.checkNull(executor);
        this.executor = executor;
    }

    @Override
    protected void performImpl(int tag, int count, LogicParam param) {
        executor.run(this, tag, count, param);
    }

    @Override
    protected void cancelImpl(int tag, boolean immediately) {
        executor.cancel(tag, immediately);
    }

}
