package com.kys.kyspartners.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.kys.kyspartners.Callbacks.VerifyAccessCodeCallback;
import com.kys.kyspartners.General;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.UpdateUser;
import com.kys.kyspartners.network.VerifyAccessCode;

import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AccessCodeActivity extends AppCompatActivity implements VerifyAccessCodeCallback {

    EditText access;
    CardView cardView;
    TextView textView;
    EditText pass1, pass2;
    String _username = "";
    General general;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_code);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        access = (EditText) findViewById(R.id.edit_access_code);
        cardView = (CardView) findViewById(R.id.card1);
        textView = (TextView) findViewById(R.id.tvEmail);
        pass1 = (EditText) findViewById(R.id.password1);
        pass2 = (EditText) findViewById(R.id.password2);
        general = new General(AccessCodeActivity.this);

        access.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String accessCode = access.getText().toString();
                    verify(accessCode);
                }
                return false;
            }
        });
    }

    private void verify(String accessCode) {
        VerifyAccessCode verifyAccessCode = new VerifyAccessCode(AccessCodeActivity.this, accessCode, this);
        verifyAccessCode.CheckAccessCode(cardView, textView);
    }

    public void UpdatePassword(View view) {
        String password1 = pass1.getText().toString();
        String password2 = pass2.getText().toString();
        String _email = textView.getText().toString();
        String access_code = "KYSP" + _username.substring(0, 2) + _email.substring(0, 2) + new Random().nextInt(9000);

        if (password1.isEmpty() || password2.isEmpty()) {
            general.error("Please all fields must be filled");
            return;
        }
        if (!password1.contentEquals(password2)) {
            general.error("Passwords do not match");
            return;
        }
        UpdateUser updateUser = new UpdateUser(AccessCodeActivity.this, _email, password2, access_code);
        updateUser.UpdateUser(AccessCodeActivity.this);
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

    @Override
    public void onAccessCode(String username) {
        _username = username;
    }
}
