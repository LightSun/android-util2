package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.heaven7.core.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

 /* after android N:
   <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.class100.android.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

        //you need to create a provider_paths.xml to /res/xml/ dir

<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
<external-path name="external_files" path="."/>
</paths>
     */
/**
 * get image from pick or camera.
 *
 * @author heaven7
 */
public class ImageHelper {

    private static final int IMAGE_REQUEST_CODE  = 100; //选择图片
    private static final int SELECT_PIC_NOUGAT   = 101; //选择图片。24+

    private static final int RESULT_REQUEST_CODE = 102; //裁剪 result
    private static final int CAMERA_REQUEST_CODE = 104; //拍照

    private static final int NONE = 0;

    private static final String IMAGE_UNSPECIFIED = "image/*";

    private final WeakReference<Activity> mWeakActivity;
    private final ImageCallback mCallback;
    private final String mDir;
    private boolean mDestroied;

    private File mCameraFile  ; //照相机的File对象
    private File mCropFile    ; //裁剪后的File对象
    private File mGalleryFile ; //相册的File对象

    /**
     * create image helper.
     * @param dir the dir to save file.
     * @param activity the activity.
     * @param mCallback the image callback.
     */
    public ImageHelper(String dir , Activity activity, ImageCallback mCallback) {
        this.mDir = dir;
        this.mWeakActivity = new WeakReference<Activity>(activity);
        this.mCallback = mCallback;

        mCameraFile = new File(dir, "IMAGE_FILE_NAME.jpg");    //照相机的File对象
        mCropFile = new File(dir, "PHOTO_FILE_NAME.jpg");      //裁剪后的File对象
        mGalleryFile = new File(dir, "IMAGE_GALLERY_NAME.jpg");//相册的File对象
    }

    /**
     * get the authority of FileProvider.
     * @param activity the activity.
     * @return the authority
     */
    protected String getAuthority(Activity activity){
        return activity.getPackageName() + ".fileprovider";
    }

    /**
     * get image by zoom
     */
    public void pick(){
        pick(null);
    }

    /**
     * get image by zoom
     */
    public void pick(String path) {
        if (mDestroied) return;

        if(path != null){
            File file = new File(path);
            if(!file.exists()){
                throw new RuntimeException("file not exit, path = " + path);
            }
            mGalleryFile = file;
        }

        final Activity activity = getActivity();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(mGalleryFile));
            intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(intent, SELECT_PIC_NOUGAT);
        } else {
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mGalleryFile));
            activity.startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    }
    /**
     * destroy this
     */
    public void destroy() {
        this.mDestroied = true;
    }

    public void camera(){
        camera(null);
    }

    /**
     * use camera to get capture image.
     * @param path absolute mPath
     */
    public void camera(String path) {
        if (mDestroied) return;
        final Activity activity = getActivity();

        if(path != null){
            File file = new File(path);
            if(!file.exists()){
                throw new RuntimeException("file not exit, path = " + path);
            }
            mCameraFile = file;
        }
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(mCameraFile));
            intentFromCapture.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            intentFromCapture.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
        }
        activity.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;

        final Activity activity = getActivity();
        if(activity == null){
            Logger.w("ImageHelper","onActivityResult","activity == null");
            return;
        }
        switch (requestCode){

            case CAMERA_REQUEST_CODE: {//照相后返回
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startPhotoZoom(getUriForFile(mCameraFile));
                } else {
                    startPhotoZoom(Uri.fromFile(mCameraFile));
                }
                break;
            }

            case RESULT_REQUEST_CODE:{
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(activity.getContentResolver()
                            .openInputStream(getUriForFile(mCropFile)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                mCallback.onSuccess(mCropFile, bitmap);
                break;
            }

            case IMAGE_REQUEST_CODE: {//版本<7.0  图库后返回
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    startPhotoZoom(uri);
                }
                break;
            }

            case SELECT_PIC_NOUGAT://版本>= 7.0
                File imgUri = new File(FilePathCompat.getFilePath(activity, data.getData()));
                startPhotoZoom(getUriForFile(imgUri));
                break;
        }
    }

    /**
     * start zoom the photo
     * @param inputUri the path which to save image file
     */
    private void startPhotoZoom(Uri inputUri) {
        if (mDestroied) return;
        final Activity activity = getActivity();
        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri outPutUri = Uri.fromFile(mCropFile);
            intent.setDataAndType(inputUri, IMAGE_UNSPECIFIED);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);

        } else {
            Uri outPutUri = Uri.fromFile(mCropFile);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String url = FilePathCompat.getFilePath(activity, inputUri); //这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                File file = new File(url);
                Logger.i("Button3Activity","startPhotoZoom","url = " + url +", "
                        + Environment.getExternalStorageDirectory().getAbsolutePath());
                Logger.w("Button3Activity","startPhotoZoom"," exist = " + file.exists());
                intent.setDataAndType(Uri.fromFile(new File(url)), IMAGE_UNSPECIFIED);
            } else {
                intent.setDataAndType(inputUri, IMAGE_UNSPECIFIED);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }

        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 64);
        intent.putExtra("outputY", 64);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠

        mCallback.buildZoomIntent(intent);

        activity.startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /*public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }*/
    /**
     * 获取图片的URI，根据不同版本生成相应的URI
     */
    private Uri getUriForFile(File file) {
        Activity activity = getActivity();
        if (activity == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(activity, getAuthority(activity), file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private Activity getActivity() {
        final Activity activity = mWeakActivity.get();
        if (activity == null)
            throw new IllegalStateException("activity is mDestroied?");
        return activity;
    }

    /**
     * the image callback
     */
    public static abstract class ImageCallback{


        /**
         * called on get image success.
         * @param mFile the saved image file
         * @param photo the photo
         */
        public abstract void onSuccess(File mFile, Bitmap photo);

        /**
         * called if you want to change intent parameter of zoom.
         * @param defaultIntent the default zoom intent
         */
        public void buildZoomIntent(Intent defaultIntent){

        }
    }

}
