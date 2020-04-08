package com.heaven7.android.util2;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by heaven7 on 2017/9/25 0025.
 * @since 1.1.8
 */

public final class FilePathCompat {

    private static final FilePathResolver sResolver;

    static {
        if(Build.VERSION.SDK_INT >= 19){
            sResolver = new FilePathResolver_kitkat();
        }else{
            sResolver = new BaseFilePathResolver();
        }
    }

    /**
     * get file path from uri.
     * @param context the context
     * @param data the data
     * @return the file path
     */
    public static String getFilePath(Context context, Uri data){
        String result = null;
        try {
            //path = /document/home:董文秀的模板17:58.pdf
            //content://com.android.providers.downloads.documents/document/home:董文秀的模板17:58.pdf
            //content://com.android.providers.downloads.documents/document/raw:/storage/emulated/0/Download/交易/app-release_219_jiagu_sign.apk
            //content://com.android.providers.downloads.documents/document/raw:/storage/emulated/0/Download/.com.google.Chrome.jfRtT6
            if (DocumentsContract.isDocumentUri(context, data)) {
                String docId = DocumentsContract.getDocumentId(data);
                if (FilePathResolver_kitkat.isExternalStorageDocument(data)
                        || FilePathResolver_kitkat.isDownloadsDocument(data)
                        || FilePathResolver_kitkat.isMediaDocument(data)
                ) {
                    String dir = docId.split(":")[0];
                    String str = docId.split(":")[1];
                    if(str.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())){
                        result = DocumentsContract.getDocumentId(data).replace(dir + ":", "");
                    }else {
                        result = Environment.getExternalStorageDirectory() +  "/Documents/"
                                + docId.substring(docId.indexOf(dir)+ dir.length()+ 1);
                    }
                    //if not exist reset.
                    if(!new File(result).exists()){
                        result = null;
                    }
                }
            }

            if(data.getPathSegments() != null && data.getPathSegments().contains("raw:")){
                result = DocumentsContract.getDocumentId(data).replace("raw:", "");
            }else {
                result = sResolver.getFilePath(context, data);
            }
        }catch (Exception e){
            //ignore
            //e.printStackTrace();
        }
        //wx
        if(result == null){
            result = getPathFromFileProvider(context, data);
        }
        return result;
    }

    private static String getPathFromFileProvider(Context context, Uri uri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                //String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                // if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                Class<FileProvider> fileProviderClass = FileProvider.class;
                                try {
                                    Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                    getPathStrategy.setAccessible(true);
                                    Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                    if (invoke != null) {
                                        String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                        Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                        Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                        getFileForUri.setAccessible(true);
                                        Object invoke1 = getFileForUri.invoke(invoke, uri);
                                        if (invoke1 instanceof File) {
                                            String filePath = ((File) invoke1).getAbsolutePath();
                                            return filePath;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface FilePathResolver {

        /**
         * get file path
         * @param context the context
         * @param data the data.
         * @return the file path.
         */
        String getFilePath(Context context, Uri data);
    }

    //low 4.4
    private static class BaseFilePathResolver implements FilePathResolver{

        @Override
        public String getFilePath(Context context, Uri data) {
            String filename = null;
            if (data.getScheme().compareTo("content") == 0) {
                Cursor cursor = context.getContentResolver().query(data,
                        new String[] { MediaStore.Images.Media.DATA }, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    filename = cursor.getString(0);
                }
            } else if (data.getScheme().toString().compareTo("file") == 0) {// file:///开头的uri
                filename = data.toString().replace("file://", "");// 替换file://
                if (!filename.startsWith("/mnt")) {// 加上"/mnt"头
                    filename += "/mnt";
                }
            }
            return filename;
        }
    }
    private static class FilePathResolver_kitkat implements FilePathResolver{

        @TargetApi(19)
        @Override
        public String getFilePath(Context context, Uri uri) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {// MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
                // (and
                // general)
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
                return uri.getPath();
            }
            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context
         *            The context.
         * @param uri
         *            The Uri to query.
         * @param selection
         *            (Optional) Filter used in the query.
         * @param selectionArgs
         *            (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = { column };
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }
}
