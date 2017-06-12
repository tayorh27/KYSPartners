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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.kys.kyspartners.General;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.RegisterShopUser;

import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    General general;
    BootstrapButton button;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        general = new General(RegisterActivity.this);

        etUsername = (EditText) findViewById(R.id.edit_username);
        etEmail = (EditText) findViewById(R.id.edit_email);
        etPassword = (EditText) findViewById(R.id.edit_password);
        button = (BootstrapButton) findViewById(R.id.btnReg);
    }

    public void AlreadyHaveAccount(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    public void RegisterUser(View view) {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            general.error("Please all fields must be filled");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            general.error("Invalid email address");
            return;
        }
        String access_code = "KYSP" + username.substring(0, 2) + email.substring(0, 2) + new Random().nextInt(5000);
        RegisterShopUser registerShopUser = new RegisterShopUser(RegisterActivity.this, username, email, password, access_code);
        registerShopUser.Register(button, RegisterActivity.this);
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
