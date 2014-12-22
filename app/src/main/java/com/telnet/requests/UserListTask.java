package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.UserListFragment;

import java.io.IOException;

public class UserListTask extends KaribouTask<String, Void, String> {

    private UserListFragment userListFragment;

    public UserListTask(UserListFragment a) {
        this.userListFragment = a;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String url = params[0];
        try {
            Log.d("UserListTask", url);

            result = doGet(url);
        } catch (IOException e) {
            Log.e("UserListTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute(String content) {
        userListFragment.setUsers(content);
    }
}
