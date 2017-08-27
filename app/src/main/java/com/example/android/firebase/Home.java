package com.example.android.firebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.android.firebase.Profile.REQUEST_CODE;
import static com.example.android.firebase.Profile.profilePic;

public class Home extends AppCompatActivity {

    private static final int REQUEST_CODE_FOR_ALL_USER = 200;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    UserAdapter adapter;
    static ArrayList<String> userName = new ArrayList<>(0);
    static ArrayList<String> userEmail = new ArrayList<>(0);
    static ArrayList<byte[]> userProfile = new ArrayList<>(0);
    static int color[] = new int[18];
    static byte placeholder[];

    DatabaseHelper helper;
    ListView listView;
    FloatingActionButton addUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        placeholder = stream.toByteArray();


        color = getResources().getIntArray(R.array.mdcolor_500);
        helper = new DatabaseHelper(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        listView = (ListView) findViewById(R.id.user_list_View);
        addUserButton = (FloatingActionButton) findViewById(R.id.button_add_user);

        if (userEmail.isEmpty()) {
            Cursor c = helper.getUser();
            if (c == null)
                Toast.makeText(this, "list is null", Toast.LENGTH_LONG).show();
            else {
                while (c.moveToNext()) {
                    userEmail.add(c.getString(0));
                    userName.add(c.getString(1));
                    userProfile.add(c.getBlob(2));
                }

            }
        }

        adapter = new UserAdapter(this);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Home.this, MessageList.class);
                intent.putExtra("user_email", userEmail.get(position));
                intent.putExtra("user_name", userName.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu menu = new PopupMenu(Home.this, view, Gravity.CENTER);
                menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete_user) {
                            helper.deleteUser(userEmail.get(position));
                            userName.remove(position);
                            userEmail.remove(position);
                            userProfile.remove(position);
                            adapter.notifyDataSetChanged();
                            return true;
                        } else if (id == R.id.block_user) {

                        }
                        return false;
                    }
                });

                return true;
            }
        });

    }

    public void addNewUser(View view) {
        Intent intent = new Intent(Home.this, AllUserActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FOR_ALL_USER);

    }


    ///////////     menu    methods     ////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.update_profile) {
            updateProfile();
            return true;
        } else if (item.getItemId() == R.id.update_status)
        {
            updateStatus();
            return true;
        }
        return false;
    }

    private void updateProfile() {
        Intent intent = new Intent(Home.this, Profile.class);
        startActivity(intent);
    }

    private void updateStatus()
    {
        startActivity(new Intent(Home.this,UpdateStatus.class));
    }

    void logout() {
        auth.signOut();


        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "sigout successfull", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Home.this, MainActivity.class));
            helper.deleteAll();
            finish();
        } else {
            Toast.makeText(this, "error in sigout", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOR_ALL_USER && resultCode == 50) {
            String email = data.getExtras().getString("email");
            String name = data.getExtras().getString("name");
            byte[] byteArray = data.getByteArrayExtra("profilePic");

            long r = helper.addUser(email, name, byteArray);
            if (r == -1)
                Toast.makeText(this, "error in adding user", Toast.LENGTH_LONG).show();
            else {
                userName.add(name);
                userEmail.add(email);
                userProfile.add(byteArray);
                Toast.makeText(this, "user added successfully", Toast.LENGTH_LONG).show();

            }
            adapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_CANCELED) ;

    }

}
