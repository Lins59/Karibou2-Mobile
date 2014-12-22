package com.telnet.requests;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public abstract class KaribouTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final String COOKIES_HEADER = "Set-Cookie";
    private static String pantieId;
    private static String login;
    private static String password;
    private static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    public KaribouTask() {
        CookieHandler.setDefault(cookieManager);
    }


    public static String doGet(String urlStr) throws IOException {
        BufferedReader in = null;
        try {
            Log.i("KaribouTask", "GET " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            //add request header
            urlConnection.setRequestProperty("User-Agent", "Bobodroid");

            // Cookies
            Object obj = cookieManager.getCookieStore().getCookies();
            String str = TextUtils.join(",", cookieManager.getCookieStore().getCookies());
            //if (cookieManager.getCookieStore().getCookies().size() > 0) {
            //    urlConnection.setRequestProperty("Cookie",
            //            TextUtils.join(",", cookieManager.getCookieStore().getCookies()));
//            }

            // Handle cookies
//            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
//            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
//            if (cookiesHeader != null) {
//                for (String cookie : cookiesHeader) {
//                    Object obj2 = HttpCookie.parse(cookie);
//                    cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
//                }
//            }

            in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String doPost(String urlStr, List<NameValuePair> params) throws IOException {
        BufferedReader in = null;
        try {
            Log.i("KaribouTask", "POST " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            urlConnection.setRequestProperty("User-Agent", "Bobodroid");
            Object obj = cookieManager.getCookieStore().getCookies();
            String str = TextUtils.join(",", cookieManager.getCookieStore().getCookies());
            //if (cookieManager.getCookieStore().getCookies().size() > 0) {
            //    urlConnection.setRequestProperty("Cookie",
            //            TextUtils.join(",", cookieManager.getCookieStore().get(null)));
            //}

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            if (params != null) {
                writer.write(getQuery(params));
            }
            writer.flush();
            writer.close();
            os.close();

            // Fire query
            // Handle cookies
            //Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            //List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            //if (cookiesHeader != null) {
            //   for (String cookie : cookiesHeader) {
            //       cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            //  }
            //}

            // Read input
            in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String getPantieId() {
        return pantieId;
    }

    public static void setPantieId(String pantieId) {
        KaribouTask.pantieId = pantieId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        KaribouTask.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        KaribouTask.password = password;
    }
}
