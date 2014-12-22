package com.telnet.karibou;

import android.util.SparseArray;

public class ColorsFactory {
    private static SparseArray<String> usersColors = new SparseArray<String>();

    public static String getColor(Integer userId) {
        // Choose a color if not already done
        String color = usersColors.get(userId);
        if (color == null) {
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            color = String.format("#%02x%02x%02x", r, g, b);

            usersColors.put(userId, color);
        }
        return color;
    }
}
