package com.telnet.karibou;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.telnet.adapters.FlashmailAdapter;
import com.telnet.objects.Flashmail;
import com.telnet.parsers.FlashmailParser;
import com.telnet.requests.FlashmailTask;
import com.telnet.requests.ReadFlashmailTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FlashmailFragment extends Fragment {
    private static final int ANSWER_CODE = 1;
    private static final int ANSWER_ID = Menu.FIRST + 3;
    private static final int MARK_READ_ID = Menu.FIRST + 4;
    private FlashmailFragment fma = this;
    private FlashmailAdapter adapter;
    private ListView flashmailList;
    private Handler handler;
    private Timer timer;
    private TimerTask doAsynchronousTask;
    private ArrayList<Integer> notificationsDisplayed = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View flashmail = inflater.inflate(R.layout.activity_flashmail, container, false);

        flashmailList = (ListView) flashmail.findViewById(R.id.flashmailList);

        // Text when list is empty
        TextView emptyText = (TextView) flashmail.findViewById(android.R.id.empty);
        flashmailList.setEmptyView(emptyText);

        // Listener when click on item list is fired
        flashmailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                answerFlashmail(adapter.getFlashmails().get(+position));
            }
        });
        adapter = new FlashmailAdapter(getActivity(), new ArrayList<Flashmail>());
        flashmailList.setAdapter(adapter);

        // Add contextual menu
        registerForContextMenu(flashmailList);

        // Timer for FM refresh will be set in onResume
        handler = new Handler();
        return flashmail;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.choose_action);
        menu.add(0, ANSWER_ID, 0, R.string.action_answer);
        menu.add(0, MARK_READ_ID, 0, R.string.action_mark_read);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //  info.position will give the index of selected item
        Flashmail selectedFlashmail = adapter.getFlashmails().get(info.position);
        switch (item.getItemId()) {
            case ANSWER_ID:
                answerFlashmail(selectedFlashmail);
                break;
            case MARK_READ_ID:
                markFlashmailRead(selectedFlashmail);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FlashmailFragment", "onResume called");

        // Execute task to refresh and change timer interval back to 10s
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        doAsynchronousTask = new FlashmailTimerTask();
        timer.schedule(doAsynchronousTask, 0, Constants.FLASHMAIL_REFRESH * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FlashmailFragment", "onPause called");

        // Change timer interval to 120s
        timer.cancel();
        timer = new Timer();
        doAsynchronousTask = new FlashmailTimerTask();
        timer.schedule(doAsynchronousTask, Constants.FLASHMAIL_PAUSE_REFRESH * 1000, Constants.FLASHMAIL_PAUSE_REFRESH * 1000);
    }

    public TimerTask getFlashmailTask() {
        return doAsynchronousTask;
    }

    public void setFlashmails(String json) {
        // Add flashmails
        List<Flashmail> flashmails = FlashmailParser.parse(json);

        // empty all elements
        adapter.clear();

        adapter.addAll(flashmails);

        // Add notification
        if (isAdded()) {
            createNotifications(flashmails);
        }

        // Refresh adapter
        adapter.notifyDataSetChanged();
    }

    public void createNotifications(List<Flashmail> flashmails) {
        if (flashmails != null) {
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            int dot = 200;      // Length of a Morse Code "dot" in milliseconds
            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 200;    // Length of Gap Between dots/dashes
            int medium_gap = 500;   // Length of Gap Between Letters
            int long_gap = 1000;    // Length of Gap Between Words

            for (Flashmail flashmail : flashmails) {
                Integer id = Integer.parseInt(flashmail.getId());
                if (!notificationsDisplayed.contains(id)) {
                    long[] vibratePattern = {0, dash, short_gap, dot, short_gap, dash, // K
                            medium_gap,
                            dot, short_gap, dot, short_gap, dash, short_gap, dash, short_gap, dash // 2
                    };
                    Intent intent = new Intent(this.getActivity(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Answer intent
                    Intent answerIntent = new Intent(this.getActivity(), SendFlashmailActivity.class);
                    answerIntent.putExtra("id", flashmail.getId());
                    answerIntent.putExtra("pseudo", flashmail.getSender().getPseudo());
                    answerIntent.putExtra("userId", flashmail.getSender().getId().toString());
                    answerIntent.putExtra("answer", true);
                    PendingIntent pAnswerIntent = PendingIntent.getActivity(getActivity(), Integer.parseInt(flashmail.getId()), answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Mark as read
                    Intent readFlashmailIntent = new Intent(this.getActivity(), ReadFlashmailActivity.class);
                    readFlashmailIntent.putExtra("id", flashmail.getId());
                    PendingIntent pReadFlashmailIntent = PendingIntent.getActivity(getActivity(), Integer.parseInt(flashmail.getId()), readFlashmailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
                    builder.setPriority(Notification.FLAG_HIGH_PRIORITY);
                    builder.setAutoCancel(true);
                    //builder.setWhen(0);
                    builder.addAction(R.drawable.ic_launcher, "Répondre", pAnswerIntent);
                    builder.addAction(R.drawable.ic_action_refresh, "Marquer comme lu", pReadFlashmailIntent);
                    builder.setContentIntent(pendingIntent);
                    builder.setContentTitle("Vous avez reçu un flashmail !");
                    builder.setContentText(flashmail.getSender().getPseudo() + " vous a envoyé : " + flashmail.getMessage());
                    builder.setSmallIcon(R.drawable.ic_launcher);
                    builder.setVibrate(vibratePattern);

                    Notification notification = builder.build();
                    notification.vibrate = vibratePattern;
                    notificationManager.notify(id, notification);
                    notificationsDisplayed.add(id);
                }
            }
        }
    }

    /*
     Actions on Menu
      */
    public void answerFlashmail(Flashmail flashmail) {
        Intent intent = new Intent(fma.getActivity().getBaseContext(), SendFlashmailActivity.class);
        intent.putExtra("id", flashmail.getId());
        intent.putExtra("pseudo", flashmail.getSender().getPseudo());
        intent.putExtra("userId", flashmail.getSender().getId().toString());
        intent.putExtra("answer", true);
        startActivityForResult(intent, ANSWER_CODE);
    }

    public void markFlashmailRead(Flashmail flashmail) {
        ReadFlashmailTask readFlashmailTask = new ReadFlashmailTask(fma);
        readFlashmailTask.execute(Constants.FLASHMAIL_READ_URL, flashmail.getId());
    }

    @Override
    // Used when SendFlashmailActivity terminates => mark FM as read
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ANSWER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String flashmailId = data.getStringExtra("id");
                markFlashmailRead(new Flashmail(flashmailId));
            }
        }
    }//onActivityResult

    private class FlashmailTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        FlashmailTask flashmailTask = new FlashmailTask(fma);
                        flashmailTask.execute(Constants.FLASHMAIL_URL);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
