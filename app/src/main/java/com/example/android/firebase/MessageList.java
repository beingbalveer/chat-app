package com.example.android.firebase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MessageList extends AppCompatActivity {

    String userEmail, userName;
    ListView listView;
    EditText editText;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference userRef;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseListAdapter<Message> adapter;
    String fromTo = "";
    Menu longClickMenu;
    DatabaseReference deleteRef;
    boolean isItemSelected = false;
    View selectedView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);



        editText = (EditText) findViewById(R.id.input_message_text);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("masseges");
        userRef = database.getReference("users");
        reference.keepSynced(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("user_email");
        userName = intent.getExtras().getString("user_name");
        getSupportActionBar().setTitle(userEmail);

        listView = (ListView) findViewById(R.id.user_list_View);


        ///////////////////////////////////////////\

        String one = user.getEmail().replace('.', '_');
        String two = userEmail.replace('.', '_');

        int c = one.compareTo(two);
        if (c < 0)
            fromTo = one + "_" + two;
        else
            fromTo = two + "_" + one;

        Query query = reference.orderByChild("fromTo").equalTo(fromTo);

        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.one_message_layout, query) {
            @Override
            protected void populateView(View v, Message model, int position) {

                RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.message_layout);
                RelativeLayout inner_layout = (RelativeLayout) v.findViewById(R.id.inner_layout);
          //      TextView userText = (TextView) v.findViewById(R.id.user_name_text);
                TextView timeText = (TextView) v.findViewById(R.id.message_time_text);
                TextView messageText = (TextView) v.findViewById(R.id.message_body_text);

                String s[] = model.getMessageText().split(" ", 2);
                messageText.setText(s[1]);
                timeText.setText(DateFormat.format("dd-MM-yyyy hh-mm-ss", model.getMessageTime()));

                if (s[0].equals(user.getEmail())) {
             //       userText.setText("Me");
                    layout.setGravity(Gravity.START);
                    messageText.setTextColor(Color.BLACK);
                    timeText.setTextColor(Color.BLACK);
                    inner_layout.setBackgroundResource(R.drawable.message_background);
                } else {
            //        userText.setText(userEmail);
                    layout.setGravity(Gravity.END);
                    messageText.setTextColor(Color.parseColor("#d6f8fB"));
                    timeText.setTextColor(Color.parseColor("#C6E8EB"));
                    inner_layout.setBackgroundResource(R.drawable.message_background1);

                }
            }
        };

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteRef = adapter.getRef(position);
                longClickMenu.getItem(0).setVisible(true);
                longClickMenu.getItem(1).setVisible(true);
                view.setBackgroundColor(Color.argb(70,82,184,255));
                RelativeLayout inner_layout = (RelativeLayout) view.findViewById(R.id.inner_layout);
                inner_layout.setBackgroundColor(Color.argb(70,82,184,255));
                isItemSelected = true;
                selectedView = view;
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteRef = adapter.getRef(position);
                longClickMenu.getItem(0).setVisible(false);
                longClickMenu.getItem(1).setVisible(false);
                view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                RelativeLayout inner_layout = (RelativeLayout) view.findViewById(R.id.inner_layout);
                inner_layout.setBackgroundResource(R.drawable.message_background);
                listView.setStackFromBottom(false);
                adapter.notifyDataSetChanged();
                isItemSelected = false;
            }
        });


        //////////////////////////////////////////////

        listView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        if (isItemSelected)
        {
            longClickMenu.getItem(0).setVisible(false);
            longClickMenu.getItem(1).setVisible(false);
            selectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            RelativeLayout inner_layout = (RelativeLayout) selectedView.findViewById(R.id.inner_layout);
            inner_layout.setBackgroundResource(R.drawable.message_background);
            isItemSelected = false;
        }
        else
            super.onBackPressed();
    }

    public void sendMessage(View view) {
        String new_message = editText.getText().toString();
        editText.setText("");

        Message message = new Message(fromTo, user.getEmail(), new_message);
        reference.push().setValue(message);
        listView.setStackFromBottom(true);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_main_menu, menu);
        if (menu!=null)
            longClickMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.message_delete) {
            deleteMessage();
            return true;
        } else if (item.getItemId() == R.id.forward_message) {

        }
        return false;
    }

    private void deleteMessage() {
        deleteRef.removeValue();
        adapter.notifyDataSetChanged();
        longClickMenu.getItem(0).setVisible(false);
        longClickMenu.getItem(1).setVisible(false);
        selectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RelativeLayout inner_layout = (RelativeLayout) selectedView.findViewById(R.id.inner_layout);
        inner_layout.setBackgroundResource(R.drawable.message_background);
    }


    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        finish();
        return true;
    }
}
