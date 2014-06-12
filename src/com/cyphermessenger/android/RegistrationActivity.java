package com.cyphermessenger.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.List;

public class RegistrationActivity extends Activity implements ContentListener, View.OnKeyListener {


    private EditText usernameTextField;
    private EditText passwordTextField;
    private EditText passwordConfirmTextField;
    private EditText captchaTextField;
    private ImageView captchaView;
    private Captcha captcha;
    private ContentManager cm;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        cm = new ContentManager(DBManagerAndroidImpl.getInstance(this), this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.messages_loading_message));

        // VIEWS
        usernameTextField = (EditText) findViewById(R.id.name_field);
        passwordTextField = (EditText) findViewById(R.id.password_field);
        passwordConfirmTextField = (EditText) findViewById(R.id.confirm_field);
        captchaTextField = (EditText) findViewById(R.id.captcha_text_field);
        progressBar = (ProgressBar) findViewById(R.id.captcha_loading_spinner);

        String[] captchaSerialized = null;
        if (savedInstanceState != null) {
            captchaSerialized = savedInstanceState.getStringArray("captchaSerialized");
        }
        if (captchaSerialized != null) {
            onCaptcha(new Captcha(captchaSerialized));
        } else {
            cm.requestCaptcha();
        }

        usernameTextField.setOnKeyListener(this);
        passwordTextField.setOnKeyListener(this);
        passwordConfirmTextField.setOnKeyListener(this);
        captchaTextField.setOnKeyListener(this);
    }

    private void sendRegistration() {
        if (!usernameTextField.getText().toString().isEmpty() && !passwordTextField.getText().toString().isEmpty() && !passwordConfirmTextField.getText().toString().isEmpty() && !captchaTextField.getText().toString().isEmpty()) {
            String uNameFromClient = usernameTextField.getText().toString();
            String passwordFromClient = passwordTextField.getText().toString();
            String confirmFromClient = passwordConfirmTextField.getText().toString();
            String captchaTextFromClient = captchaTextField.getText().toString();
            if (!captcha.verify(captchaTextFromClient)) {
                AUtils.shortToast(R.string.registration_captcha_invalid, this);
            } else if (passwordFromClient.length() < 8) {
                AUtils.shortToast(R.string.registration_password_too_short, this);
            } else if (!passwordFromClient.equals(confirmFromClient)) {
                AUtils.shortToast(R.string.registration_password_dont_match, this);
            } else if (uNameFromClient.length() <= 3) {
                AUtils.shortToast(R.string.registration_username_too_short, this);
            } else {
                cm.register(uNameFromClient, passwordFromClient, captchaTextFromClient, captcha);
                progressDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.register_button) {
            sendRegistration();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        String uNameFromClient = usernameTextField.getText().toString();
        String passwordFromClient = passwordTextField.getText().toString();
        String confirmFromClient = passwordConfirmTextField.getText().toString();
        String captchaTextFromClient = captchaTextField.getText().toString();
        savedState.putString("userNameClient", uNameFromClient);
        savedState.putString("passwordClient", passwordFromClient);
        savedState.putString("passwordConfirmationClient", confirmFromClient);
        savedState.putString("captchaValue", captchaTextFromClient);
        savedState.putBoolean("wasDialogShowing", progressDialog.isShowing());
        if (captcha != null) {
            savedState.putStringArray("captchaSerialized", captcha.toStringArray());
        }
        super.onSaveInstanceState(savedState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        usernameTextField.setText(savedState.getString("userNameClient"));
        passwordTextField.setText(savedState.getString("passwordClient"));
        passwordConfirmTextField.setText(savedState.getString("passwordConfirmationClient"));
        captchaTextField.setText(savedState.getString("captchaValue"));
        if (savedState.getBoolean("wasDialogShowing")) {
            progressDialog.show();
        }
    }


    @Override
    public void onLogged(CypherUser user) {
        // Start AlarmManager for notifications
        new ContentUpdateManager(this).startDefaultReceiver();

        // Load data from server
        cm.pullAllSync();

        progressDialog.dismiss();
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCaptcha(final Captcha _captcha) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                byte[] captchaBytes = _captcha.getCaptchaImage();
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.outMimeType = "image/png";
                Bitmap captchaBitmap = BitmapFactory.decodeByteArray(captchaBytes, 0, captchaBytes.length, opt);
                captcha = _captcha;
                captchaView = (ImageView) findViewById(R.id.captcha_image);
                captchaView.setImageBitmap(captchaBitmap);
                progressBar.setVisibility(View.GONE);
                captchaView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onServerError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                AUtils.shortToast(R.string.error_general_error, getApplicationContext());
            }
        });
    }

    @Override
    public void onCaptchaInvalid() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                captchaView.setVisibility(View.GONE);
                cm.requestCaptcha();
                captchaTextField.getText().clear();
                AUtils.shortToast(R.string.registration_captcha_invalid, getApplicationContext());
            }
        });
    }

    @Override
    public void onUsernameTaken() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                captchaView.setVisibility(View.GONE);
                cm.requestCaptcha();
                captchaTextField.getText().clear();
                usernameTextField.getText().clear();
                AUtils.shortToast(R.string.registration_username_already_taken, getApplicationContext());
            }
        });
    }

    @Override
    public boolean onKey(View v, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (i == KeyEvent.KEYCODE_ENTER) {
                sendRegistration();
            }
        }
        return false;
    }


    @Override
    public void onSessionInvalid() {

    }

    @Override
    public void onFindUser(List<String> list) {

    }

    @Override
    public void onContactChange(CypherContact contact) {

    }

    @Override
    public void onContactDeleted(String name) {

    }

    @Override
    public void onUsernameNotFound() {

    }

    @Override
    public void onLoginInvalid() {

    }

    @Override
    public void onContactNotFound() {

    }

    @Override
    public void onContactWaiting() {

    }

    @Override
    public void onContactBlocked() {

    }

    @Override
    public void onContactDenied() {

    }

    @Override
    public void onMessageSent(CypherMessage message) {

    }

    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {

    }

    @Override
    public void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {

    }

    @Override
    public void onPullKeys(List<ECKey> keys, long notifiedUntil) {

    }

    @Override
    public void onGetMessages(List<CypherMessage> messages) {

    }
}

