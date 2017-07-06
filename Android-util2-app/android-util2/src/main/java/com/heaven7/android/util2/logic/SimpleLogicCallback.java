package com.heaven7.android.util2.logic;

/**
 * Created by heaven7 on 2017/6/19 0019.
 */

public abstract class SimpleLogicCallback extends LogicAction.LogicCallback {

    @Override
    public final void onLogicResult(LogicAction action, int resultCode, int tag, LogicParam param) {
        switch (resultCode){
            case LogicAction.RESULT_SUCCESS:
                onSuccess(action, tag, param);
                break;

            case LogicAction.RESULT_FAILED:
                onFailed(action, tag, param);
                break;

            default:
                onLogicResultIml(action, resultCode, tag, param);
        }
    }

    protected void onLogicResultIml(LogicAction action, int resultCode, int tag, LogicParam param) {

    }

    protected void onSuccess(LogicAction action, int tag, LogicParam param) {

    }
    protected void onFailed(LogicAction action, int tag, LogicParam param) {

    }
}
