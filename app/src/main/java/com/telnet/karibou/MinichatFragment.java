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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.telnet.adapters.MinichatAdapter;
import com.telnet.objects.Message;
import com.telnet.parsers.MinichatParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MinichatFragment extends Fragment {
    private HttpToolbox httpToolbox;
    private EditText msg;
    private ImageButton send;
    private MinichatAdapter adapter;
    private ListView messagesList;
    private boolean atBottom = true;
    private boolean stopPush = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create http toolbox
        httpToolbox = HttpToolbox.getInstance(getActivity().getApplicationContext());

        // View
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

        // Get messages list immediately
        PrioritizedStringRequest minichatRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.MC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String messages) {
                setMessages(messages);
                Log.i("MinichatFragment", "End of polling minichat");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("MinichatFragment", "Error in polling");
            }
        });
        minichatRequest.setPriority(Request.Priority.IMMEDIATE);
        httpToolbox.addToRequestQueue(minichatRequest, "MC");

        // Engage push task
        this.stopPush = false;
        engagePushTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MinichatFragment", "onPause called");

        // Cancel push task
        this.stopPush = true;
    }

    public void setMessages(String messages) {
        adapter.clear();
        appendMessages(messages);
        scrollToBottom();
    }

    public void sendMessage(final String message) {
        Log.d("PostTask", message);

        PrioritizedStringRequest sendMessageRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.MC_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SendMessageRequest", "Message posted");
                clearForm();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    Toast.makeText(getActivity().getApplicationContext(), "Timeout",
                            Toast.LENGTH_LONG).show();
                } else if (volleyError instanceof AuthFailureError) {
                    //TODO
                } else if (volleyError instanceof ServerError) {
                    //TODO
                } else if (volleyError instanceof NetworkError) {
                    //TODO
                } else if (volleyError instanceof ParseError) {
                    //TODO
                }
                Log.e("SendMessageRequest", "Error when sending message.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("msg", message);
                return params;
            }
        };
        sendMessageRequest.setPriority(Request.Priority.IMMEDIATE);
        httpToolbox.addToRequestQueue(sendMessageRequest, "PUSH");
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
            //Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
            //getActivity().startActivity(intent);
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
        } catch (Exception e) {
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

    public void engagePushTask() {
        if (!stopPush) {
            PrioritizedStringRequest pushRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.KARIBOU_PUSH, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("PushRequest", response);
                    appendMessagesFromPantie(response);

                    // Reengage push task
                    engagePushTask();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Timeout",
                                Toast.LENGTH_LONG).show();
                    } else if (volleyError instanceof AuthFailureError) {
                        //TODO
                    } else if (volleyError instanceof ServerError) {
                        //TODO
                    } else if (volleyError instanceof NetworkError) {
                        //TODO
                    } else if (volleyError instanceof ParseError) {
                        //TODO
                    }
                    Log.e("PushRequest", "Error during pushing.");
                    engagePushTask();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("session", HttpToolbox.getPantieId());
                    return params;
                }
            };
            pushRequest.setPriority(Request.Priority.IMMEDIATE);
            pushRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 0));
            httpToolbox.addToRequestQueue(pushRequest, "PUSH");
        }
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
