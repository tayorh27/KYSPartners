package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AccountActivity extends AppCompatActivity {

    ImageView iv;
    TextView username, email, phone;
    AppData data;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new AppData(AccountActivity.this);
        User user = data.getUser();

        iv = (ImageView) findViewById(R.id.imageViewDp);
        username = (TextView) findViewById(R.id.tvUsername);
        email = (TextView) findViewById(R.id.tvEmail);
        phone = (TextView) findViewById(R.id.tvPhone);

        username.setText(user.username);
        email.setText(user.email);
        phone.setText("Confirmed");

        String username = user.username;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        //int color2 = generator.getColor("user@gmail.com")
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .height(128)
                .width(128)
                .toUpperCase()
                .endConfig()
                .buildRoundRect(String.valueOf(username.charAt(0)), color1, 64);
        iv.setImageDrawable(textDrawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_change) {
            startActivity(new Intent(AccountActivity.this, ChangePasswordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
