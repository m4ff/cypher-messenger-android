package com.cyphermessenger.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.cyphermessenger.client.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class MessagesActivity extends MainActivity {

    final TreeSet<CypherMessage> messagesSet = new TreeSet<>();
    ListView messageListView;
    MessageAdapter adapter;
    CypherContact contact;
    CypherUser user;
    EditText editor;
    ImageButton buttonSend;


    private static final int DEFAULT_MESSAGE_NUM = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();
        editor = (EditText) findViewById(R.id.message_editor);
        buttonSend = (ImageButton) findViewById(R.id.message_button_send);

        if(intent != null) {
            contact = cm.getContactByID(intent.getLongExtra("CONTACT", 0));
            user = cm.getUser();
        }

        if(contact != null) {
            setTitle(contact.getUsername());
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(contact.getUsername().hashCode());
        }

        if(contact.getStatus().equals(CypherContact.BLOCKED)) {
            getLayoutInflater().inflate(R.layout.activity_messages_contact_blocked, null);
        } else if(contact.getStatus().equals(CypherContact.DENIED) ) {
            getLayoutInflater().inflate(R.layout.activity_messages_user_blocked, null);
        }

        messageListView = (ListView) findViewById(R.id.messages_list_view);

        adapter = new MessageAdapter(messagesSet, new CypherMessage[] {});
        messageListView.setAdapter(adapter);

        editor.setImeOptions(EditorInfo.IME_ACTION_DONE);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact == null) {
                    showTopToast(R.string.error_general_error);
                    return;
                }
                String text = editor.getText().toString();
                if(!text.equals("")) {
                    synchronized (messagesSet) {
                        CypherMessage newMessage = cm.sendMessage(contact, text);
                        messagesSet.add(newMessage);
                        adapter.notifyDataSetChanged();
                        messageListView.smoothScrollToPosition(messagesSet.size() - 1);
                    }
                } else {
                    AUtils.shortToast(R.string.empty_message, getApplicationContext());
                }
                editor.setText("");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateManager.setActiveContact(contact.getUserID());
        if(contact != null) {
            cm.getMessages(contact, 0, DEFAULT_MESSAGE_NUM);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateManager.unsetActiveContact();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(contact.getStatus().equals(CypherContact.BLOCKED)) {
            getMenuInflater().inflate(R.menu.messages_user_blocked, menu);
        } else {
            getMenuInflater().inflate(R.menu.messages, menu);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message_contact_block:
                showToast(R.string.messages_contact_blocked);
                break;
            case R.id.message_delete_conversation:
                //showToast(R.string.messages_delete_conversation);
                //messagesSet.clear();
                //adapter.notifyDataSetChanged();
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MessageAdapter extends TreeSetAdapter<CypherMessage> {


        public MessageAdapter(TreeSet<CypherMessage> treeSet, CypherMessage[] treeSetArray) {
            super(treeSet, treeSetArray);
        }

        @Override
        public long getItemId(int i) {
            return treeSetArray[i].getMessageID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CypherMessage nowConsidering = getItem(position);


            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View template = inflater.inflate(R.layout.messages_template, null);

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
            messageDisplaying.setTextColor(Color.BLACK);
            return template;
        }
    }

    @Override
    public void onMessageSent(CypherMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewMessages(HashMap<Long, List<CypherMessage>> message) {
        super.onNewMessages(message);
        List<CypherMessage> messages = message.get(contact.getUserID());
        if(messages != null) {
            messagesSet.addAll(messages);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            showToast(R.string.messages_new_message);
        }
    }

    @Override
    public void onGetMessages(final List<CypherMessage> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesSet.clear();
                messagesSet.addAll(messages);
                adapter.notifyDataSetChanged();
                messageListView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onContactBlocked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editor.setEnabled(false);
                editor.setAlpha(0.8f);
                editor.setHint(R.string.prompt_text_message_hint_contact_blocked);

                buttonSend.setEnabled(false);
                buttonSend.setClickable(false);
                buttonSend.setAlpha(0.8f);

                adapter.notifyDataSetChanged();
            }
        });
    }
}
