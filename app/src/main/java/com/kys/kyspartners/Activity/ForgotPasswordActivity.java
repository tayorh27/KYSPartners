package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.kys.kyspartners.General;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.ForgotPasswordServer;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    General general;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void RequestPassword(View view) {
        String email_add = email.getText().toString();
        if (email_add.isEmpty()) {
            general.error("Please fill email address");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_add).matches()) {
            general.error("Invalid email address");
            return;
        }
        ForgotPasswordServer forgotPasswordServer = new ForgotPasswordServer(ForgotPasswordActivity.this, email_add);
        forgotPasswordServer.CheckUser();
    }

    public void AccessCode(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, AccessCodeActivity.class));
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
