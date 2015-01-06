package com.telnet.karibou;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.telnet.adapters.MinichatAdapter;
import com.telnet.objects.Message;
import com.telnet.parsers.MinichatParser;
import com.telnet.requests.MinichatTask;
import com.telnet.requests.PostMessage;
import com.telnet.requests.PushTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class MinichatFragment extends Fragment {
    private EditText msg;
    private ImageButton send;
    private MinichatFragment mca = this;
    private MinichatAdapter adapter;
    private ListView messagesList;
    private boolean atBottom = true;
    private PushTask pushTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View minichat = inflater.inflate(R.layout.activity_minichat, container, false);

        this.messagesList = (ListView) minichat.findViewById(R.id.messagesList);
        this.send = (ImageButton) minichat.findViewById(R.id.send);
        this.msg = (EditText) minichat.findViewById(R.id.messageField);

        adapter = new MinichatAdapter(getActivity(), new ArrayList<Message>(60));
        messagesList.setAdapter(adapter);
        messagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    atBottom = true;
                } else {
                    atBottom = false;
                }
            }
        });

        this.msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    scrollToBottom();
                }
            }
        });
        // Send a message listener
        this.send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = msg.getText().toString();
                sendMessage(message);
            }
        });

        return minichat;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MinichatFragment", "onResume called");

        MinichatTask minichatTask = new MinichatTask(this);
        minichatTask.execute(Constants.MC_URL);

        pushTask = new PushTask(this);
        pushTask.execute(Constants.KARIBOU_PUSH);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MinichatFragment", "onPause called");

        // Cancel timer
        pushTask.cancel(true);
    }

    public void setMessages(String messages) {
        adapter.clear();
        appendMessages(messages);
        scrollToBottom();
    }
    public void sendMessage(String message) {
        new PostMessage(mca).execute(Constants.MC_POST, message);
    }

    // First insert (GET from /mc2/state/nb,msg)
    public void appendMessages(String json) {
        try {
            ArrayList<Message> messages = MinichatParser.parse(json);
            adapter.addAll(messages);
            adapter.notifyDataSetChanged();

            if (atBottom) {
                scrollToBottom();
            }
        } catch (JSONException e) {
            // If a JSONException is raised, it must have been a logout from TELnet
            Log.e("MinichatActivity", e.getMessage());

            // Relaunch LoginActivity
            Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
            getActivity().startActivity(intent);
        }
    }

    // Append with Pantie (we need to transform data)
    public void appendMessagesFromPantie(String json) {
        try {
            // Don't process timeout messages :
            // Timeout = JSONObject
            // Normal messages = JSONArray
            Object jsonToken = new JSONTokener(json).nextValue();
            if (jsonToken instanceof JSONArray) {
                JSONArray ja = new JSONArray(json);
                JSONObject obj = (JSONObject) ja.get(0);
                String r = "[" + obj.get("data") + "]";
                appendMessages(r);
            } else {
                // Must be timeout
            }
        } catch (JSONException e) {
            // If a JSONException is raised, it must have been a logout from TELnet
            Log.e("MinichatActivity", e.getMessage());

            // Relaunch LoginActivity
            Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
            getActivity().startActivity(intent);
        }
    }

    public void clearForm() {
        this.msg.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.msg.getWindowToken(), 0);
        this.messagesList.setFocusable(true);
        this.msg.setText("");
        scrollToBottom();
    }

    public void setPushTask(PushTask pushTask) {
        this.pushTask = pushTask;
    }

    public void scrollToBottom() {
        messagesList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messagesList.setSelection(adapter.getCount() - 1);
            }
        });
    }
}
