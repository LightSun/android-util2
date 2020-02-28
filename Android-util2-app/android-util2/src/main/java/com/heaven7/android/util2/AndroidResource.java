package com.heaven7.android.util2;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ArrayRes;

import com.heaven7.java.base.util.Predicates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2017/8/11 0011.
 * @since 1.0.4
 */

public class AndroidResource {

    /**
     * the default resource id = 0.
     */
    public static final int EMPTY_RES_ID = 0;

    /**
     * load any object array from known resource.eg:
     * @param context the context
     * @param <T> the object type
     * @return the object list
     */
    public static <T> List<T> loadObjectArray(Context context, @ArrayRes int arrayId, ObjectFactory<T> factory){
        // Get the array of objects from the `warm_up_descs` array
        final TypedArray statuses = context.getResources().obtainTypedArray(arrayId);
        final List<T> categoryList = new ArrayList<>();
        TypedArray rawStatus = null;
        try {
            final int arrSize = statuses.length();
            for (int i = 0; i < arrSize; i++) {
                int statusId = statuses.getResourceId(i, EMPTY_RES_ID);
                // Get the properties of one object
                rawStatus = context.getResources().obtainTypedArray(statusId);

                T[] ts = factory.create(rawStatus);
                if(!Predicates.isEmpty(ts)){
                    for(T t: ts){
                        categoryList.add(t);
                    }
                }
                rawStatus.recycle();
                rawStatus = null;
            }
        } finally {
            statuses.recycle();
            if (rawStatus != null) {
                rawStatus.recycle();
            }
        }
        return categoryList;
    }

    /**
     * the object factory
     * @param <T> the object type
     * @since 1.0.4
     */
    public interface ObjectFactory<T>{

        /**
         * create object array from the type array. that is often we want to build one or more object from  one resource.
         * and the TypeArray will auto recycled . {@linkplain #loadObjectArray(Context, int, ObjectFactory)}.
         * <p>such as:
         * <code><array name="arr_audio_single_select">
         * <item>2_4</item>
         * <item>@drawable/prepare_icon_02</item>
         * <item>@string/audio_single_select_main_desc</item>
         * <item>@string/select_image_minor_desc</item>
         * </array> </code> </p>
         * @param ta the type array
         * @return the object array.
         * @see #loadObjectArray(Context, int, ObjectFactory)
         */
        T[]  create(TypedArray ta);
    }
}
