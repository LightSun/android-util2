package com.heaven7.android.util2.logic;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * the logic parameter
 * Created by heaven7 on 2017/6/17 0017.
 */

public class LogicParam implements Parcelable{

    private int mPosition;
    private Parcelable mParceData;
    private Serializable mSerData;

    public LogicParam setPosition(int position){
        this.mPosition = position;
        return this;
    }
    public LogicParam setParceableData(Parcelable data){
        this.mParceData = data;
        return this;
    }
    public LogicParam setSerializableData(Serializable data){
        this.mSerData = data;
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

    public Parcelable getParceableData() {
        return mParceData;
    }
    public Serializable getSerializableData(){
        return mSerData;
    }

    @Override
    public String toString() {
        return "LogicParam{" +
                "mPosition=" + mPosition +
                ", mParceData=" + mParceData +
                ", mSerData=" + mSerData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicParam that = (LogicParam) o;

        if (mPosition != that.mPosition) return false;
        if (mParceData != null ? !mParceData.equals(that.mParceData) : that.mParceData != null)
            return false;
        return mSerData != null ? mSerData.equals(that.mSerData) : that.mSerData == null;

    }

    @Override
    public int hashCode() {
        int result = mPosition;
        result = 31 * result + (mParceData != null ? mParceData.hashCode() : 0);
        result = 31 * result + (mSerData != null ? mSerData.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPosition);
        dest.writeParcelable(this.mParceData, flags);
        dest.writeSerializable(this.mSerData);
    }

    public LogicParam() {
    }

    protected LogicParam(Parcel in) {
        this.mPosition = in.readInt();
        this.mParceData = in.readParcelable(Parcelable.class.getClassLoader());
        this.mSerData = in.readSerializable();
    }

    public static final Creator<LogicParam> CREATOR = new Creator<LogicParam>() {
        @Override
        public LogicParam createFromParcel(Parcel source) {
            return new LogicParam(source);
        }

        @Override
        public LogicParam[] newArray(int size) {
            return new LogicParam[size];
        }
    };
}
