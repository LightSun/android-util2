package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * stack over flow of android.
 * https://stackoverflow.com/documentation/android/103/intent#t=201707140757431233948
 * Created by heaven7 on 2017/7/14 0014.
 */

public class IntentHelper {

    //share multiple files through intent
    public static void share(AppCompatActivity context, List<String> paths) {

        if (paths == null || paths.size() == 0) {
            return;
        }
        ArrayList<Uri> uris = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        for (String path : paths) {
            File file = new File(path);
            uris.add(Uri.fromFile(file));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(intent);
    }


    //Starting a File Chooser Activity
    public void showFileChooser(Activity context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        intent.setType("*/*");

        // Update with additional mime types here using a String[].
       //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // REQUEST_CODE = <some-integer>
       // context.startActivityForResult(intent, REQUEST_CODE);
    }

   // Opening with the default browser
    public void onBrowseClick(View v) {
        Context context = v.getContext();
        String url = "http://www.google.com";
        url = adjustUrl(url);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Here we use an intent without a Chooser unlike the next example
            context.startActivity(intent);
        }
    }
    //he user to select a browser
    public void onBrowseClick2(View v) {
        String url = "http://www.google.com";
        url = adjustUrl(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        // Note the Chooser below. If no applications match,
        // Android displays a system message.So here there is no need for try-catch.
        v.getContext().startActivity(Intent.createChooser(intent, "Browse with"));
    }

    //must start with http:// or https://
    private String adjustUrl(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")){
            url = "http://" + url;
        }
        return url;
    }


}
