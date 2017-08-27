package com.example.android.firebase;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.firebase.Home.color;
import static com.example.android.firebase.Home.userName;
import static com.example.android.firebase.Home.userProfile;

public class UserAdapter extends BaseAdapter{

    Context context;
    static String placeholderString = "";

    UserAdapter(Context context)
    {
        this.context = context;

    }

    @Override
    public int getCount() {
        return userName.size();
    }

    @Override
    public Object getItem(int position) {
        return userName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_user_item,parent,false);

        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        TextView status_text = (TextView) convertView.findViewById(R.id.text2);
        TextView thumbnail = (TextView) convertView.findViewById(R.id.thumbnail_text);
        CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.thumbnail_image);
        textView.setText(userName.get(position));
 //       status_text.setText();

        Bitmap bitmap = BitmapFactory.decodeByteArray(userProfile.get(position),0,userProfile.get(position).length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        if (placeholderString.equals(Arrays.toString(byteArray)))
        {
            String s = "" + userName.get(position).charAt(0);
            thumbnail.setText(s.toUpperCase());
            Random r = new Random();
            GradientDrawable bgShape = (GradientDrawable)thumbnail.getBackground();
            bgShape.setColor(color[r.nextInt(18)]);

            thumbnail.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            imageView.setImageBitmap(bitmap);
            thumbnail.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
