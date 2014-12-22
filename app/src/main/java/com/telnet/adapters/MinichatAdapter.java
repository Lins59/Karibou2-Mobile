package com.telnet.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.telnet.karibou.R;
import com.telnet.objects.Message;

import java.util.List;

public class MinichatAdapter extends ArrayAdapter<Message> {
    private final Activity context;
    private final List<Message> messages;

    public MinichatAdapter(Activity context,
                           List<Message> messages) {
        super(context, R.layout.single_minichat, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.single_minichat, parent, false);
        TextView messageView = (TextView) rowView.findViewById(R.id.message);

        Message message = messages.get(position);
        String hours = Integer.toString(message.getDate().getHours());
        if (hours.length() < 2) {
            hours = "0" + hours;
        }
        String minutes = Integer.toString(message.getDate().getMinutes());
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        messageView.setText(Html.fromHtml("<font color=\"#C3C3C3\">[" + hours + ":" +
                minutes + "]</font> " +
                "<font color=\"" + message.getColor() + "\" >" +
                message.getPseudo() + "</font> : " +
                message.getMessage() + "<br />"));

        if (position % 2 == 1) {
            rowView.setBackgroundColor(view.getResources().getColor(R.color.k2_light_blue));
        } else {
            rowView.setBackgroundColor(Color.WHITE);
        }
        return rowView;
    }
}