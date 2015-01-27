package com.telnet.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.telnet.karibou.ColorFactory;
import com.telnet.karibou.HttpToolbox;
import com.telnet.karibou.ImageFactory;
import com.telnet.karibou.R;
import com.telnet.objects.User;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private final Activity context;
    private final List<User> users;
    private ImageLoader imageLoader;

    public UserListAdapter(Activity context,
                           List<User> users) {
        super(context, R.layout.single_user, users);
        this.context = context;
        this.users = users;
        this.imageLoader = HttpToolbox.getInstance(this.getContext()).getImageLoader();
    }

    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.single_user, parent, false);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        User user = users.get(position);
        String away = "";
        if (user.isAway()) {
            away = " (Absent) ";
            rowView.setBackgroundColor(view.getResources().getColor(R.color.k2_light_blue));
        }
        String message = "";
        if (user.getMessage() != null && !user.getMessage().equals("null")) {
            message = user.getMessage();
        }
        // Parse user
        String htmlUser = "<font color=\"" + ColorFactory.getColor(user.getId()) + "\" >" +
                user.getPseudo() + "</font> " + "<i>" + away + message + "</i>" + "<br />";

        txtTitle.setText(Html.fromHtml(htmlUser));

        ImageFactory.colorizePicture(imageView, user.getId(), user.getPicturePath());
        return rowView;
    }
}