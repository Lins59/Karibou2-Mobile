package com.telnet.karibou;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;
import java.util.Map;

public class ImageFactory {
    private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();
    private static SparseArray<Bitmap> images_id = new SparseArray<Bitmap>();

    public static void colorizePicture(final ImageView imageView, final int id, final String picturePath) {
        // Get image if not already done
        Bitmap picture = images.get(picturePath);
        if (picture == null) {
            ImageRequest imageRequest = new ImageRequest(picturePath,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            setPicture(imageView, picturePath, bitmap, id);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            imageView.setImageResource(R.drawable.k2);
                        }
                    });
            HttpToolbox.getInstance(imageView.getContext()).getRequestQueue().add(imageRequest);
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
