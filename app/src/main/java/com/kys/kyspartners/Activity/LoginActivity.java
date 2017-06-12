package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.GetLoginFromServer;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPass;
    General general;
    AppData data;
    BootstrapButton button;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        general = new General(LoginActivity.this);
        data = new AppData(LoginActivity.this);
        etUsername = (EditText) findViewById(R.id.edit_username);
        etPass = (EditText) findViewById(R.id.edit_pass);
        button = (BootstrapButton) findViewById(R.id.btnLog);
    }

    public void DontHaveAccount(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    public void ForgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    public void LoginUser(View view) {
        String username = etUsername.getText().toString();
        String password = etPass.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            general.error("Please all fields must be filled");
            return;
        }
        GetLoginFromServer getLoginFromServer = new GetLoginFromServer(LoginActivity.this, username, password);
        getLoginFromServer.LoginUser(button, LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (data.getUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, OwnerActivity.class));
            finish();
        }
    }
}
