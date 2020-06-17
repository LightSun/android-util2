package com.heaven7.android.util2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.heaven7.java.base.util.FileUtils;

import java.io.File;

/**
 * the gallery utils.
 * @since 1.3.4
 */
public final class GalleryUtils {

    public static boolean insertImage(Context context, String path, int width, int height){
        //in samxing c5 , worked. but can't be scanned by mine. ImagePickLib
        /*try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    path, FileUtils.getFileName(path), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, path);
            values.put(MediaStore.Images.Media.WIDTH, width);
            values.put(MediaStore.Images.Media.HEIGHT, height);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, FileUtils.getFileName(path));
            values.put(MediaStore.Images.Media.SIZE, new File(path).length());
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //Logger.i(TAG, "onBindData","save bitmap ok. path = " + path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //notify system
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
        return true;
    }
}
