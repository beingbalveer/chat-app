package com.example.android.firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.android.firebase.UserAdapter.placeholderString;

public class AllUserActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;

    FirebaseListAdapter<User> adapter;
    ListView listView;
    ProgressBar progressBar;
    int color[] = new int[18];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("All User");
        }

        color = getResources().getIntArray(R.array.mdcolor_500);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.all_user_list_View);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        adapter = new FirebaseListAdapter<User>(this, User.class, R.layout.one_user_item, reference) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView textView = (TextView) v.findViewById(R.id.text1);
                TextView thumbnail = (TextView) v.findViewById(R.id.thumbnail_text);
                TextView status_text = (TextView) v.findViewById(R.id.text2);
                CircleImageView imageView = (CircleImageView) v.findViewById(R.id.thumbnail_image);

                TextView email = (TextView) v.findViewById(R.id.email);
                email.setText(model.email);
                textView.setText(model.name);
                if (!model.status.equals(""))
                    status_text.setText(model.status);

                if (model.profilePic.isEmpty()) {
                    String s = "" + model.email.charAt(0);
                    thumbnail.setText(s.toUpperCase());
                    Random r = new Random();
                    GradientDrawable bgShape = (GradientDrawable)thumbnail.getBackground();
                    bgShape.setColor(color[r.nextInt(18)]);

                    BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    placeholderString = Arrays.toString(byteArray);

                    thumbnail.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                }else
                {
                    byte[] data = Base64.decode(model.profilePic, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                    imageView.setImageBitmap(bitmap);

                    thumbnail.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                if (model.email.equals(user.getEmail())) {
                    v.setVisibility(View.GONE);
                }
            }
        };

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (view != null) {
                    TextView textView = (TextView) view.findViewById(R.id.text1);
                    TextView emailview = (TextView) view.findViewById(R.id.email);
                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.thumbnail_image);
                    String name = textView.getText().toString();
                    String email = emailview.getText().toString();
                    Intent intent = new Intent(AllUserActivity.this,Home.class);

                    BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    intent.putExtra("profilePic",byteArray);
                    intent.putExtra("email",email);
                    intent.putExtra("name",name);
                    setResult(50,intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        finish();
        return true;
    }
}
