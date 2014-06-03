package com.cyphermessenger.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.List;


public class MessagesActivity extends Activity implements ContentListener {

    List<CypherMessage> messagesList;
    ListView messageListView = null;
    int message_id = 26;
    boolean is_User = false;
    MessageAdapter adapter = null;
    ContentManager cm;
    CypherContact contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        cm = new ContentManager(DBManagerAndroidImpl.getInstance(getApplicationContext()), this);

        if(savedInstanceState != null) {
            contact = cm.getContactByID(savedInstanceState.getLong("CONTACT"));
        }

        if(contact != null) {
            setTitle(contact.getUsername());
        }

        messageListView = (ListView) findViewById(R.id.messages_list_view);
        messagesList = cm.getMessageList(contact);

        adapter = new MessageAdapter(this, R.id.message_bubble, messagesList);
        messageListView.setAdapter(adapter);

        final EditText messageEditor = (EditText) findViewById(R.id.message_editor);
        ImageButton buttonSend = (ImageButton) findViewById(R.id.message_button_send);

        messageEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
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

    private class MessageAdapter extends ArrayAdapter<CypherMessage> {

        public MessageAdapter(Context context, int textViewResourceId, List<CypherMessage> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CypherMessage nowConsidering = getItem(position);


            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View template = inflater.inflate(R.layout.messages_template, null);

            int width = parent.getWidth() - 100;

            LinearLayout messageDisplayingLayout = (LinearLayout) template.findViewById(R.id.message_bubble);
            TextView messageDisplaying = (TextView) template.findViewById(R.id.message_bubble_text);
            messageDisplaying.setText(nowConsidering.getText());

            if(nowConsidering.isSender()) {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_user);
                messageDisplayingLayout.setGravity(Gravity.RIGHT);
                messageDisplayingLayout.setPadding(dpToPx(60), 0, 0, 0);
            } else {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_contat);
                messageDisplayingLayout.setGravity(Gravity.LEFT);
                messageDisplayingLayout.setPadding(0, 0, dpToPx(60), 0);
            }

            return template;
        }

        public int dpToPx(int dp) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return px;
        }
    }

    @Override
    public void onContactWaiting() {

    }

    @Override
    public void onMessageSent(CypherMessage message) {

    }

    @Override
    public void onGetMessages(List<CypherMessage> messages) {

    }


    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {

    }

    @Override
    public void onServerError() {

    }


    @Override
    public void onFindUser(List<String> list) {

    }

    @Override
    public void onContactChange(CypherContact contact) {

    }

    @Override
    public void onSessionInvalid() {

    }


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
    public void onLoginInvalid() {}
    @Override
    public void onContactNotFound() {}
    @Override
    public void onContactBlocked() {}
    @Override
    public void onContactDenied() {}
}
