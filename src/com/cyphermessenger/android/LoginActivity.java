package com.cyphermessenger.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.List;


public class LoginActivity extends Activity implements ContentListener {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText name = (EditText) findViewById(R.id.name_field);
        final EditText password = (EditText) findViewById(R.id.password_field);

        final Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        final Button registration = (Button) findViewById(R.id.button_registration);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registrationIntent);
            }
        });

        final Button login = (Button) findViewById(R.id.button_login);
        final ContentManager contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(this), this);
        final Context that = this;
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _name = name.getText().toString();
                String _pass = password.getText().toString();
                if(!_name.isEmpty() && !_pass.isEmpty()) {
                    contentManager.login(_name, _pass);
                } else {
                    Toast t = new Toast(that);
                    t.makeText(that, R.string.login_toast_failed, Toast.LENGTH_LONG);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginInvalid() {
        Toast t = new Toast(this);
        t.makeText(this, R.string.login_toast_failed, Toast.LENGTH_LONG);
        EditText password = (EditText) findViewById(R.id.password_field);
        password.getText().clear();
    }

    @Override
    public void onLogged(CypherUser user) {
        Intent contactIntent = new Intent(this, ContactsActivity.class);
        contactIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(contactIntent);
        finish();
    }

    @Override
    public void onCaptcha(Captcha captcha) {}
    @Override
    public void onCaptchaInvalid() {}
    @Override
    public void onUsernameTaken() {}
    @Override
    public void onUsernameNotFound() {}
    @Override
    public void onMessageSent(CypherMessage message) {}
    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {}
    @Override
    public void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {}
    @Override
    public void onPullKeys(List<ECKey> keys, long notifiedUntil) {}
    @Override
    public void onGetMessages(List<CypherMessage> messages) {}
    @Override
    public void onFindUser(List<String> list) {}
    @Override
    public void onContactChange(CypherContact contact) {}
    @Override
    public void onServerError() {}
    @Override
    public void onSessionInvalid() {}
    @Override
    public void onContactNotFound() {}
    @Override
    public void onContactWaiting() {}
    @Override
    public void onContactBlocked() {}
    @Override
    public void onContactDenied() {}
}