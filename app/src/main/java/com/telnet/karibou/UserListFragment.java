package com.telnet.karibou;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.telnet.adapters.UserListAdapter;
import com.telnet.objects.User;
import com.telnet.parsers.UserListParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserListFragment extends Fragment {
    private ListView userList;
    private UserListFragment ula = this;
    private UserListAdapter adapter;
    private Handler handler;
    private Timer timer;
    private TimerTask doAsynchronousTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View userlistview = inflater.inflate(R.layout.activity_userlist, container, false);

        userList = (ListView) userlistview.findViewById(R.id.userList);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                User u = adapter.getUsers().get(+position);
                Intent intent = new Intent(ula.getActivity().getBaseContext(), SendFlashmailActivity.class);
                intent.putExtra("pseudo", u.getPseudo());
                intent.putExtra("userId", u.getId().toString());
                startActivity(intent);
            }
        });
        adapter = new UserListAdapter(getActivity(), new ArrayList<User>(), R.drawable.enveloppe);
        userList.setAdapter(adapter);

        // Timer for refresh will be set in onResume
        handler = new Handler();

        // show The Image
        //new DownloadImageTask((ImageView) userlistview.findViewById(R.id.imageView))
        //        .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
        return userlistview;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("UserListFragment", "onResume called");

        // Execute task to refresh and change timer interval back to 10s
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        doAsynchronousTask = new UserListTimerTask();
        timer.schedule(doAsynchronousTask, 0, Constants.USER_LIST_REFRESH * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("UserListFragment", "onPause called");

        // Cancel timer
        timer.cancel();
    }

    public void setUsers(String json) {
        // Add users
        List<User> users = UserListParser.parse(json);

        // empty all elements
        adapter.clear();

        adapter.addAll(users);

        // Refresh adapter
        adapter.notifyDataSetChanged();
    }

    private class UserListTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        com.telnet.requests.UserListTask userListTask = new com.telnet.requests.UserListTask(ula);
                        userListTask.execute(Constants.USER_LIST_URL);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}

