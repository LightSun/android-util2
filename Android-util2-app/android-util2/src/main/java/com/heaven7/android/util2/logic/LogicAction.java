package com.heaven7.android.util2.logic;

/**
 * the logic state.
 * @author heaven7
 */
public interface LogicAction extends ContextData {

    /**
     * the result code indicate success.
     */
    int RESULT_SUCCESS = 1;
    /**
     * the result code indicate failed.
     */
    int RESULT_FAILED  = 2;

    /**
     * get the logic parameter by target tag
     *
     * @param tag the tag
     * @return the logic parameter
     */
    LogicParam getLogicParameter(int tag);

    /**
     * set the logic callback
     *
     * @param callback the logic callback
     */
    void addStateCallback(LogicCallback callback);

    /**
     * remove the logic callback
     *
     * @param callback the logic callback.
     */
    void removeStateCallback(LogicCallback callback);

    /**
     * perform this logic action.
     *
     * @param tag   the tag of this state.
     * @param param the logic param.
     */
    void perform(int tag, LogicParam param);

    /**
     * cancel this logic immediately or not.
     *
     * @param tag         the tag of this task.
     * @param immediately true to cancel immediately. current often is true.
     */
    void cancel(int tag, boolean immediately);

    /**
     * dispatch the tag result by target code. subclass should call this in {@linkplain #perform(int, LogicParam)}
     * or relative method.
     *
     * @param tag        the tag
     * @param resultCode the result code.
     * @return true if dispatch success, false otherwise.
     */
    boolean dispatchResult(int resultCode, int tag);


    /**
     * the logic callback
     */
    abstract class LogicCallback{

        /**
         * called on logic start
         * @param action the logic action
         * @param tag the tag
         * @param param the logic parameter
         */
        public abstract void onLogicStart(LogicAction action,int tag, LogicParam param);

        /**
         * called on logic result
         * @param action the logic action
         * @param resultCode the result code. like {@linkplain LogicAction#RESULT_SUCCESS} and etc.
         * @param tag the tag
         * @param param the logic parameter
         */
        public abstract void onLogicResult(LogicAction action, int resultCode, int tag, LogicParam param);
    }
}