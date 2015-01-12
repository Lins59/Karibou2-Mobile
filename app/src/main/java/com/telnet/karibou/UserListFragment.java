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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        adapter = new UserListAdapter(getActivity(), new ArrayList<User>());
        userList.setAdapter(adapter);

        // Timer for refresh will be set in onResume
        handler = new Handler();
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
        PrioritizedStringRequest userListRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.USER_LIST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String users) {
                setUsers(users);
                Log.i("UserListFragment", "End of polling users");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("UserListFragment", "Error in polling");
            }
        });
        userListRequest.setPriority(Request.Priority.NORMAL);
        HttpToolbox.getInstance(getActivity().getApplicationContext()).addToRequestQueue(userListRequest, "USER_LIST");
        //timer.schedule(doAsynchronousTask, 0, Constants.USER_LIST_REFRESH * 1000);
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
}

