package com.telnet.karibou;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.widget.ImageView;

import com.telnet.requests.DownloadImageTask;

import java.util.HashMap;
import java.util.Map;

public class ImagesFactory {
    private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();
    private static SparseArray<Bitmap> images_id = new SparseArray<Bitmap>();

    public static void colorizePicture(ImageView imageView, int id, String picturePath) {
        // Choose a color if not already done
        Bitmap picture = images.get(picturePath);
        if (picture == null) {
            // Bitmap will be set on PostExecute
            new DownloadImageTask(imageView).execute(picturePath, String.valueOf(id));
        } else {
            imageView.setImageBitmap(picture);
        }
    }

    public static void setPicture(ImageView imageView, String picturePath, Bitmap bitmap, int id) {
        images.put(picturePath, bitmap);
        images_id.put(id, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public static Bitmap getPicture(int userId) {
        return images_id.get(userId);
    }
}
