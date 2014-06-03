package com.cyphermessenger.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.List;


public class LoginActivity extends Activity implements ContentListener {

    private Toast toast;
    private ContentManager contentManager;
    private ProgressDialog progressDialog;
    private EditText name;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message));
        toast = new Toast(this);

        name = (EditText) findViewById(R.id.name_field);
        password = (EditText) findViewById(R.id.password_field);
        contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(this), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        contentManager.interruptRequests();
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
        switch(item.getItemId()) {
            case R.id.home :
                break;
            case R.id.action_settings:
                return true;
            case R.id.action_login:
                final Context that = getApplicationContext();
                String _name = name.getText().toString();
                String _pass = password.getText().toString();
                if (!"".equals(_name) && !"".equals(_pass)) {
                    progressDialog.show();
                    contentManager.login(_name, _pass);
                } else {
                    toast.makeText(that, R.string.login_toast_failed, Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.action_register:
                final Intent registrationIntent = new Intent(this, RegistrationActivity.class);
                startActivity(registrationIntent);
                return true;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginInvalid() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.makeText(getApplicationContext(), R.string.login_toast_failed, Toast.LENGTH_LONG).show();
                ((EditText) findViewById(R.id.password_field)).getText().clear();
            }
        });
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