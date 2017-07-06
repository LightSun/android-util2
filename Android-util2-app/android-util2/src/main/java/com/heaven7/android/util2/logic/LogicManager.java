package com.heaven7.android.util2.logic;


import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * the logic manager help we handle an order logic tasks.
 * it can run a sequence/parallel tasks, no matter sync or async.
 * @author heaven7
 */
public final class LogicManager extends ContextDataImpl {

    private static final String TAG = "LogicManager";
    private final SparseArray<LogicTask> mLogicMap;

    /*
          type    secondary     index
     * 0x ff      ff            ffff
     */
    private static final int MASK_MAIN      = 0x0000ffff;
    private static final int MASK_SECONDARY = 0x00ff0000;
    private static final int MASK_TYPE      = 0xff000000;

    private static final int SHIFT_TYPE      = 24;
    private static final int SHIFT_SECONDARY = 16;
    private static final int TYPE_PARALLEL   = 1;

    private static final int MAX_PARALLEL_COUNT  = 0xff;
    private int mIndex;

    public LogicManager() {
        this.mLogicMap = new SparseArray<>(3);
    }

    /**
     * create a logic task.
     * @param tag the tag
     * @param action the logic action
     * @param lp the logic parameter
     * @return the logic task.
     */
    public static LogicTask createTask(int tag, LogicAction action, LogicParam lp) {
        return new LogicTask(tag, action, lp);
    }

    /**
     * create a simple logic task.
     * @param runner the logic runner
     * @param lp the logic parameter
     * @return the logic task.
     */
    public static LogicTask createSimpleTask(LogicRunner runner, LogicParam lp) {
        return new LogicTask(0, new SimpleLogicAction(runner), lp);
    }

    /**
     * cancel the all task immediately which is running.
     */
    public void cancelAllImmediately(){
        cancelAll(true);
    }
    /**
     * cancel the all task which is running.
     */
    public void cancelAll(){
        cancelAll(false);
    }
    /**
     * cancel the task which is assigned by target key.
     * @param key the key . see {@linkplain #executeParallel(List, Runnable)} or {@linkplain #executeSequence(List, Runnable)}.
     */
    public void cancel(int key) {
        cancel(key, false);
    }

    /**
     * cancel the task immediately which is assigned by target key.
     * @param key the key . see {@linkplain #executeParallel(List, Runnable)} or {@linkplain #executeSequence(List, Runnable)}.
     */
    public void cancelImmediately(int key){
        cancel(key, true);
    }

    /**
     * indicate the target key of tasks is running or not.
     * @param key the key of last operation
     * @return true if is running.
     */
    public boolean isRunning(int key){
        final int type = (key & MASK_TYPE) >> 24;
        switch (type){
            case TYPE_PARALLEL:
                int baseKey = key & MASK_MAIN ;
                int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
                synchronized (mLogicMap) {
                    for (int i = 0; i < count; i++) {
                        if(mLogicMap.get(baseKey + i) != null){
                            return true;
                        }
                    }
                }
                break;

            case 0:
                synchronized (mLogicMap){
                    return mLogicMap.get(key) != null;
                }
        }
        return false;
    }

    /**
     * indicate the target task is running or not.
     * @param task the logic task.
     * @return true if is running.
     */
    public boolean isRunning(LogicTask task){
        synchronized (mLogicMap){
            return mLogicMap.indexOfValue(task, false) >= 0;
        }
    }

    /**
     * executeSequence the tasks in Parallel.
     * @param parallels the all tasks.
     * @param endAction the end action , called when all task done.
     * @return the key if this operation.
     */
    public int executeParallel(LogicTask[] parallels, Runnable endAction) {
        return executeParallel(Arrays.asList(parallels), endAction);
    }

    /**
     * executeSequence the tasks in Parallel.
     * @param parallels the all tasks.
     * @param endAction the end action , called when all task done.
     * @return the key if this operation.
     */
    public int executeParallel(List<LogicTask> parallels, Runnable endAction) {
        Throwables.checkEmpty(parallels);
        if(parallels.size() > MAX_PARALLEL_COUNT){
            throw new UnsupportedOperationException("max parallel count must below " + MAX_PARALLEL_COUNT);
        }
        final int count = parallels.size();
        int baseKey =  ++mIndex;
        mIndex += count;
        final ParallelCallback callback = new ParallelCallback(baseKey, parallels, endAction);
        final Object data = getContextData();
        for(LogicTask task : parallels){
            task.action.setContextData(data);
            task.action.addStateCallback(callback);
            task.perform();
        }
        return baseKey + count << SHIFT_SECONDARY + TYPE_PARALLEL << SHIFT_TYPE;
    }

    /**
     * execute the tasks in sequence with the end action.
     * @param tag the tag
     * @param   logicAction state performer
     * @param lp the logic parameter
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(int tag, LogicAction logicAction, LogicParam lp, Runnable endAction) {
        return executeSequence(new LogicTask[] {createTask(tag, logicAction, lp)}, endAction);
    }

    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task     the logic task
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task, Runnable endAction) {
        return executeSequence(new LogicTask[]{task}, endAction);
    }
    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task1     the logic task1
     * @param task2     the logic task2
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task1, LogicTask task2, Runnable endAction) {
        return executeSequence(new LogicTask[]{task1, task2}, endAction);
    }
    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task1     the logic task1
     * @param task2     the logic task2
     * @param task3     the logic task3
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task1, LogicTask task2, LogicTask task3, Runnable endAction) {
        return executeSequence(new LogicTask[]{task1, task2, task3}, endAction);
    }

    /**
     *execute the tasks in sequence with the end action.
     *
     * @param tasks     the logic tasks
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask[] tasks, Runnable endAction) {
        return executeSequence(Arrays.asList(tasks), endAction);
    }
    /**
     * execute the tasks in sequence with the end action.
     *
     * @param tasks     the logic tasks
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(List<LogicTask> tasks, Runnable endAction) {
        Throwables.checkNull(tasks);
        if (tasks.size() == 0) {
            throw new IllegalArgumentException("must assign a logic action");
        }
        final int key = (++mIndex);

        performStateImpl(tasks, key, 0, endAction);
        return key;
    }


    private void performStateImpl(List<LogicTask> tasks, int key, int currentIndex, Runnable endAction) {
        final LogicTask target = tasks.get(currentIndex);
        target.action.setContextData(getContextData());
        target.action.addStateCallback(new InternalCallback(tasks, key, currentIndex, endAction));
        target.perform();
    }

    //this key is blur
    private void cancel(int key, boolean immediately) {
        final int type = (key & MASK_TYPE) >> 24;
        switch (type){
            case TYPE_PARALLEL:
                int baseKey = key & MASK_MAIN ;
                int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
                for(int i = 0 ; i < count ; i++){
                    cancelByRealKey(baseKey + i, immediately);
                }
                break;

            case 0:
                cancelByRealKey(key, immediately);
        }
    }

    private void cancelByRealKey(int realKey, boolean immediately){
        LogicTask task;
        synchronized (mLogicMap) {
            task = mLogicMap.get(realKey);
            if (task != null) {
                mLogicMap.remove(realKey);
            }
        }
        if(task != null){
            task.cancel(immediately);
        }else{
            Logger.w(TAG,"cancelByRealKey","cancel task .but key not exists , key = " + realKey);
        }
    }

    private void cancelAll(boolean immediately) {
        synchronized (mLogicMap) {
            final int size = mLogicMap.size();
            for (int i = 0; i < size; i++) {
                mLogicMap.valueAt(i).cancel(immediately);
            }
            mLogicMap.clear();
        }
    }

    private class ParallelCallback extends SimpleLogicCallback{

        final int key;
        final List<LogicTask> parallelTasks;
        final Runnable endAction;
        final AtomicInteger finishCount;

        public ParallelCallback(int key, List<LogicTask> parallelTasks, Runnable endAction) {
            this.key = key;
            this.parallelTasks = parallelTasks;
            this.endAction = endAction;
            this.finishCount = new AtomicInteger(0);
        }

        private int getTaskCount(){
            return parallelTasks.size();
        }

        @Override
        public void onLogicStart(LogicAction action, int tag, LogicParam param) {
            int count = getTaskCount();
            synchronized (mLogicMap) {
                for(int i = 0 ; i < count ; i++){
                    mLogicMap.put(key + i, parallelTasks.get(i));
                }
            }
        }
        @Override
        protected void onSuccess(LogicAction action, int tag, LogicParam param) {
            removeTask(new LogicTask(tag, action, param));
            int count = finishCount.incrementAndGet();
            if(count == getTaskCount() && endAction != null){
                endAction.run();
            }
        }

        @Override
        protected void onFailed(LogicAction action, int tag, LogicParam param) {
            removeTask(new LogicTask(tag, action, param));
        }

        private void removeTask(LogicTask task){
            synchronized (mLogicMap) {
                int index = mLogicMap.indexOfValue(task, false);
                if(index >= 0){
                    mLogicMap.removeAt(index);
                   // Logger.i(TAG,"removeTask","task is removed . task = " + task);
                }
            }
            task.action.removeStateCallback(this);
        }
    }

    private class InternalCallback extends SimpleLogicCallback {

        final int key;
        final int curIndex;
        final List<LogicTask> mTasks;
        final Runnable endAction;

        public InternalCallback(List<LogicTask> tasks, int key, int currentIndex, Runnable endAction) {
            this.key = key;
            this.curIndex = currentIndex;
            this.mTasks = tasks;
            this.endAction = endAction;
        }

        @Override
        public void onLogicStart(LogicAction action, int tag, LogicParam param) {
            synchronized (mLogicMap) {
                mLogicMap.put(key, mTasks.get(curIndex));
            }
        }

        @Override
        protected void onSuccess(LogicAction action, int tag, LogicParam param) {
            removeTask();
            if (curIndex == mTasks.size() - 1) {
                //all end
                Logger.i(TAG, "onStateEnd", " all state tasks perform done. Tasks = " + mTasks);
                if(endAction != null) {
                    endAction.run();
                }
            } else {
                //perform next
                performStateImpl(mTasks, key, curIndex + 1, endAction);
            }
        }
        @Override
        protected void onFailed(LogicAction action, int tag, LogicParam param) {
            removeTask();
        }
        private void removeTask(){
            synchronized (mLogicMap) {
                mLogicMap.remove(key);
            }
            //unregister.
            mTasks.get(curIndex).action.removeStateCallback(this);
        }
    }

    public static class LogicTask {
        final int tag;
        final LogicAction action;
        final LogicParam logicParam;

        LogicTask(int tag, LogicAction action, LogicParam logicParam) {
            this.tag = tag;
            this.action = action;
            this.logicParam = logicParam;
        }

        void perform() {
            action.perform(tag, logicParam);
        }

        void cancel(boolean immediately) {
            action.cancel(tag, immediately);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LogicTask logicTask = (LogicTask) o;

            if (tag != logicTask.tag)
                return false;
            if (action != null ? !action.equals(logicTask.action) : logicTask.action != null)
                return false;
            return logicParam != null ? logicParam.equals(logicTask.logicParam) : logicTask.logicParam == null;

        }

        @Override
        public int hashCode() {
            int result = tag;
            result = 31 * result + (action != null ? action.hashCode() : 0);
            result = 31 * result + (logicParam != null ? logicParam.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "LogicTask{" +
                    "tag=" + tag +
                    ", action=" + action +
                    ", logicParam=" + logicParam +
                    '}';
        }
    }

}
