package com.cyphermessenger.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.LinkedList;
import java.util.List;


public class MessagesActivity extends MainActivity {

    final List<CypherMessage> messagesList = new LinkedList<>();
    ListView messageListView = null;
    MessageAdapter adapter = null;
    CypherContact contact;

    private static final int DEFAULT_MESSAGE_NUM = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        if(savedInstanceState != null) {
            contact = cm.getContactByID(savedInstanceState.getLong("CONTACT"));
        }

        if(contact != null) {
            setTitle(contact.getUsername());
        }

        messageListView = (ListView) findViewById(R.id.messages_list_view);

        adapter = new MessageAdapter(this, R.id.message_bubble, messagesList);
        messageListView.setAdapter(adapter);

        final EditText messageEditor = (EditText) findViewById(R.id.message_editor);
        ImageButton buttonSend = (ImageButton) findViewById(R.id.message_button_send);

        messageEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact == null) {
                    showToast(R.string.error_general_error);
                    return;
                }
                String text = ((EditText) v).getText().toString();
                synchronized (messagesList) {
                    CypherMessage newMessage = cm.sendMessage(contact, text);
                    messagesList.add(newMessage);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(contact != null) {
            cm.getMessages(contact, 0, DEFAULT_MESSAGE_NUM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                if(nowConsidering.isSent()) {
                    messageDisplaying.setBackgroundResource(R.drawable.message_bubble_user);
                } else {
                    messageDisplaying.setBackgroundResource(R.drawable.message_tmp_bubble_user);
                }
                messageDisplayingLayout.setGravity(Gravity.RIGHT);
                messageDisplayingLayout.setPadding(dpToPx(60), 0, 0, 0);
            } else {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_contat);
                messageDisplayingLayout.setGravity(Gravity.LEFT);
                messageDisplayingLayout.setPadding(0, 0, dpToPx(60), 0);
            }

            return template;
        }
    }

    @Override
    public void onMessageSent(CypherMessage message) {
        synchronized (messagesList) {
            int pos = messagesList.indexOf(message);
            messagesList.set(pos, message);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

        @Override
    public void onGetMessages(final List<CypherMessage> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesList.clear();
                messagesList.addAll(messages);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
