package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.UpdateUserPassword;

import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText access;
    CardView cardView;
    TextView textView;
    EditText old_pass, pass1, pass2;
    General general;
    User user;
    AppData data;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        access = (EditText) findViewById(R.id.edit_access_code);
        cardView = (CardView) findViewById(R.id.card1);
        textView = (TextView) findViewById(R.id.tvEmail);
        old_pass = (EditText) findViewById(R.id.old_password);
        pass1 = (EditText) findViewById(R.id.password1);
        pass2 = (EditText) findViewById(R.id.password2);
        data = new AppData(ChangePasswordActivity.this);
        general = new General(ChangePasswordActivity.this);
        user = data.getUser();
    }

    public void UpdatePassword(View view) {
        String oldPass = old_pass.getText().toString();
        String password1 = pass1.getText().toString();
        String password2 = pass2.getText().toString();
        String _email = user.email;
        String _username = user.username;
        String access_code = "KYSP" + _username.substring(0, 1) + _email.substring(0, 1) + new Random().nextInt(9000);

        if (oldPass.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            general.error("Please all fields must be filled");
            return;
        }
        if (!password1.contentEquals(password2)) {
            general.error("Passwords do not match");
            return;
        }
        if (!user.password.contentEquals(oldPass)) {
            general.error("Incorrect old password");
            return;
        }
        UpdateUserPassword updateUser = new UpdateUserPassword(ChangePasswordActivity.this, _email, password2, access_code);
        updateUser.UpdatePassword(ChangePasswordActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
