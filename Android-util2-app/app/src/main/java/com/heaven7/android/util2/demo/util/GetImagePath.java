package com.heaven7.android.util2.demo.util;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Administrator on 2017/9/22 0022.
 */

public class GetImagePath {


    //may be content:// or file:///
    public static String getPath(Context context, Uri inputUri) {
        String temp = inputUri.toString();
        if(temp.startsWith("file://")){
            return temp.substring("file://".length());
        }

        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context,
                inputUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
