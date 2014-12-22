package com.telnet.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.telnet.karibou.R;
import com.telnet.objects.Flashmail;
import com.telnet.objects.User;

import java.text.SimpleDateFormat;
import java.util.List;

public class FlashmailAdapter extends ArrayAdapter<Flashmail> {
    private static SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy 'à' HH:mm");
    private final Activity context;
    private final List<Flashmail> flashmails;

    public FlashmailAdapter(Activity context,
                            List<Flashmail> flashmails) {
        super(context, R.layout.single_user, flashmails);
        this.context = context;
        this.flashmails = flashmails;
    }

    public List<Flashmail> getFlashmails() {
        return this.flashmails;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.single_flashmail, parent, false);
        TableRow firstRow = (TableRow) rowView.findViewById(R.id.firstRow);
        TextView senderView = (TextView) rowView.findViewById(R.id.sender);
        TextView dateView = (TextView) rowView.findViewById(R.id.date);
        TextView answerInfo = (TextView) rowView.findViewById(R.id.answerInfo);
        TextView previousMessageView = (TextView) rowView.findViewById(R.id.previousMsg);
        TextView messageView = (TextView) rowView.findViewById(R.id.message);

        Flashmail flashmail = flashmails.get(position);
        User user = flashmail.getSender();
        String htmlUser = "<font color=\"" + user.getColor() + "\" >" +
                user.getPseudo() + "</font>";

        senderView.setText(Html.fromHtml(htmlUser));
        dateView.setText(formater.format(flashmail.getDate()));
        if (flashmail.getOldMessage() != null) {
            answerInfo.setVisibility(View.VISIBLE);
            previousMessageView.setVisibility(View.VISIBLE);
            previousMessageView.setText(flashmail.getOldMessage());
        }
        messageView.setText(flashmail.getMessage());
        return rowView;
    }
}