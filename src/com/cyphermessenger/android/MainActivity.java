package com.cyphermessenger.android;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;
import android.support.v4.app.FragmentActivity;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements ContentListener, NotificationListener {

    /** TODO
     * RegistrationActivity: back button
     * LoginActivity: spostare bottoni in alto
     */

    protected ContentManager cm;
    protected ContentUpdateManager updateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cm = new ContentManager(DBManagerAndroidImpl.getInstance(this), this);
        updateManager = new ContentUpdateManager(this, this);
        if(cm.getSession() == null) {
            Intent login = new Intent(this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateManager.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateManager.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cm.interruptRequests();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void showToast(final int str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AUtils.shortToast(str, getApplicationContext());
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // ContentListener
    @Override
    public void onServerError() {
        showToast(R.string.error_general_error);
    }

    @Override
    public void onSessionInvalid() {
        showToast(R.string.error_general_error);
        cm.login();
    }

    @Override
    public void onLoginInvalid() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onGetMessages(final List<CypherMessage> messages) {}
    @Override
    public void onMessageSent(CypherMessage message) {}
    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {}
    @Override
    public void onFindUser(List<String> list) {}
    @Override
    public void onContactWaiting() {}
    @Override
    public void onLogged(CypherUser user) {}
    @Override
    public void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {}
    @Override
    public void onPullKeys(List<ECKey> keys, long notifiedUntil) {}
    @Override
    public void onCaptcha(Captcha captcha) {}
    @Override
    public void onCaptchaInvalid() {}
    @Override
    public void onUsernameTaken() {}
    @Override
    public void onUsernameNotFound() {}
    @Override
    public void onContactNotFound() {}
    @Override
    public void onContactBlocked() {}
    @Override
    public void onContactDenied() {}
    @Override
    public void onContactChange(CypherContact contact) {}

    // NotificationListener
    @Override
    public void onNewContacts(List<CypherContact> contacts) {}
    @Override
    public void onNewMessages(HashMap<Long, List<CypherMessage>> message) {}
    @Override
    public void onNewKeys() {}

}
