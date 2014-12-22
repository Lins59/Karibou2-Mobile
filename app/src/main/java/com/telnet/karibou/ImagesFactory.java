package com.telnet.karibou;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.telnet.requests.DownloadImageTask;

import java.util.HashMap;
import java.util.Map;

public class ImagesFactory {
    private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();

    public static void colorizePicture(ImageView imageView, String picturePath) {
        // Choose a color if not already done
        Bitmap picture = images.get(picturePath);
        if (picture == null) {
            // Bitmap will be set on PostExecute
            new DownloadImageTask(imageView).execute(picturePath);
        } else {
            imageView.setImageBitmap(picture);
        }
    }

    public static void setPicture(ImageView imageView, String picturePath, Bitmap bitmap) {
        images.put(picturePath, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
