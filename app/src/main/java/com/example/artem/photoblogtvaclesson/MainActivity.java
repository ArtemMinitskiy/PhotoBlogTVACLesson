package com.example.artem.photoblogtvaclesson;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    private FloatingActionButton postBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        postBtn = (FloatingActionButton) findViewById(R.id.add_post_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PhotoBlog");

        mAuth = FirebaseAuth.getInstance();

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(postIntent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.btn_log_out:
                logout();
                return true;
            case R.id.btn_settings:
                Intent intentSettings = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(intentSettings);
                return true;
//            case R.id.btn_search:

            default:
                return false;
        }

    }

    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null){
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }else {

        }

    }
}
