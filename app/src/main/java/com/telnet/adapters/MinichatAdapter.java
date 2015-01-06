package com.telnet.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.telnet.karibou.R;
import com.telnet.karibou.SmileyFactory;
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

        StringBuilder sb = new StringBuilder();
        sb.append("<font color=\"#C3C3C3\">[" + hours + ":" +
                minutes + "]</font> ");
        // Add /me emote
        if (message.getMessage().startsWith("/me")) {
            sb.insert(0, "<i>");
            // <font>
            sb.append("<font color=\"");
            sb.append(message.getColor());
            sb.append("\" >");
            // Pseudo
            sb.append(message.getPseudo());
            // </font>
            sb.append("</font>");
            // Message
            sb.append(message.getMessage().substring("/me".length()));
            sb.append("<br />");
            sb.append("</i >");
        } else if (message.getMessage().startsWith("/life")) {
            sb.insert(0, "<i>");
            sb.append("La vie suivait son cours et ");
            // <font>
            sb.append("<font color=\"");
            sb.append(message.getColor());
            sb.append("\" >");
            // Pseudo
            sb.append(message.getPseudo());
            // </font>
            sb.append("</font>");
            sb.append(" se demandait pourquoi 42.");
            sb.append("</i>");
        } else if (message.getMessage().startsWith("/joke")) {
            sb.insert(0, "<i>");
            sb.append("Tout allait bien et, soudain, ");
            // <font>
            sb.append("<font color=\"");
            sb.append(message.getColor());
            sb.append("\" >");
            // Pseudo
            sb.append(message.getPseudo());
            // </font>
            sb.append("</font>");
            sb.append(" fit une blague.");
            sb.append("</i>");
        } else {
            // <font>
            sb.append("<font color=\"");
            sb.append(message.getColor());
            sb.append("\" >");
            // Pseudo
            sb.append(message.getPseudo());
            // </font>
            sb.append("</font> : ");
            // Message
            sb.append(message.getMessage());
            sb.append("<br />");
        }
        Spannable enrichedText = SmileyFactory.getSmiledText(rowView.getContext(), sb.toString());

        messageView.setText(enrichedText);

        if (position % 2 == 1) {
            rowView.setBackgroundColor(view.getResources().getColor(R.color.k2_light_blue));
        } else {
            rowView.setBackgroundColor(Color.WHITE);
        }
        return rowView;
    }
}