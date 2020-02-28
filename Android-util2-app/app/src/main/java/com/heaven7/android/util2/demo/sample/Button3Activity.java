package com.heaven7.android.util2.demo.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.heaven7.android.util2.demo.R;
import com.heaven7.android.util2.demo.util.GetImagePath;
import com.heaven7.core.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class Button3Activity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.bt_toast_normal)
    Button button30;

    @BindView(R.id.bt_toast_warn)
    Button button31;

    @BindView(R.id.iv_photo)
    ImageView iv_photo;


    String path = Environment.getExternalStorageDirectory()+"/Android/data/com.heaven7.android.util2.demo/files/";
    File mCameraFile = new File(path, "IMAGE_FILE_NAME.jpg");//照相机的File对象
    File mCropFile = new File(path, "PHOTO_FILE_NAME.jpg");//裁剪后的File对象
    File mGalleryFile = new File(path, "IMAGE_GALLERY_NAME.jpg");//相册的File对象

    private String mPackName = "com.heaven7.android.util2.demo";
    private String mProvoderPath = "com.heaven7.android.util2.demo.fileprovider";

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int SELECT_PIC_NOUGAT = 101;
    private static final int RESULT_REQUEST_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test_toast);

        ButterKnife.bind(this);

        button30.setOnClickListener(this);
        button31.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_toast_normal:{


                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
                    Uri uriForFile = FileProvider.getUriForFile(this, mProvoderPath, mCameraFile);
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                    intentFromCapture.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intentFromCapture.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
                }
                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);

                break;
            }
            case R.id.bt_toast_warn:{
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
                    Uri uriForFile = FileProvider.getUriForFile
                            (this, mProvoderPath, mGalleryFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                    intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, SELECT_PIC_NOUGAT);
                } else {
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mGalleryFile));
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {//照相后返回
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri inputUri = FileProvider.getUriForFile(this, mProvoderPath, mCameraFile);//通过FileProvider创建一个content类型的Uri

                    startPhotoZoom(inputUri);//设置输入类型
                } else {
                    Uri inputUri = Uri.fromFile(mCameraFile);
                    startPhotoZoom(inputUri);
                }
                break;
            }
            case IMAGE_REQUEST_CODE: {//版本<7.0  图库后返回
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    //crop(uri);
                    startPhotoZoom(uri);
                }
                break;
            }
            case SELECT_PIC_NOUGAT://版本>= 7.0
                File imgUri = new File(GetImagePath.getPath(this, data.getData()));
                Logger.w("Button3Activity","onActivityResult"," exist = " + imgUri.exists() + " ," + imgUri);
                Uri dataUri = FileProvider.getUriForFile
                        (this, mProvoderPath, imgUri);
                // Uri dataUri = getImageContentUri(data.getData());
                startPhotoZoom(dataUri);
                break;
            case RESULT_REQUEST_CODE:{
                Uri inputUri = FileProvider.getUriForFile(this, mProvoderPath, mCropFile);//通过FileProvider创建一个content类型的Uri
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(inputUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Bitmap bitmap = data.getParcelableExtra("data");
                iv_photo.setImageBitmap(bitmap);
                break;
            }

        }
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 裁剪图片方法实现
     */
    public void startPhotoZoom(Uri inputUri) {
        if (inputUri == null) {
            Logger.e("error","The uri is not exist.");
            return;
        }
        Logger.i("Button3Activity","startPhotoZoom","inputUri " + inputUri);

        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri outPutUri = Uri.fromFile(mCropFile);
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);

        } else {
            Uri outPutUri = Uri.fromFile(mCropFile);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String url = GetImagePath.getPath(this, inputUri); //这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                File file = new File(url);
                Logger.i("Button3Activity","startPhotoZoom","url = " + url +", "
                        + Environment.getExternalStorageDirectory().getAbsolutePath());
                Logger.w("Button3Activity","startPhotoZoom"," exist = " + file.exists());
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(inputUri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }


        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
        intent.putExtra("outputFormat", "JPEG");
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        startActivityForResult(intent, RESULT_REQUEST_CODE);//这里就将裁剪后的图片的Uri返回了
    }


}