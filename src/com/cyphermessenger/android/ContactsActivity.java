package com.cyphermessenger.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.cyphermessenger.client.*;

import java.util.*;

public class ContactsActivity extends MainActivity {

    private ListView mainView;
    //private final List<CypherContact> contactList = new LinkedList<>();
    private TreeSetAdapter<CypherContact> adapter;
    private View contactView;

    private final TreeSet<CypherContact> contactSet = new TreeSet<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity that = this;
        contactSet.addAll(cm.getContactList());
        //contactList.addAll(cm.getContactList());

        setContentView(R.layout.activity_contacts);
        mainView = (ListView) findViewById(R.id.contacts_list);

        if(contactSet.size() == 0) {
            setContentView(R.layout.activity_contacts_no_contact);
        }


        adapter = new ContactsAdapter(contactSet, new CypherContact[] {});
        mainView.setAdapter(adapter);

        mainView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final CypherContact contact = adapter.getItem(position);
                String status = contact.getStatus();

                if(status.equals(CypherContact.WAITING)) {
                    if (!contact.isFirst()) {
                        String[] actions = new String[]{"Accept", "Decline"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(that);
                        builder.setTitle(R.string.contact_waiting_actions)
                                .setItems(actions, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which) {
                                            case 0:
                                                cm.addContact(contact.getUsername());
                                                break;
                                            case 1:
                                                cm.blockContact(contact.getUsername());
                                        }
                                    }
                                });
                        builder.create().show();
                    } else {
                        String[] actions = new String[]{"Delete"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(that);
                        builder.setTitle(R.string.contact_waiting_actions)
                                .setItems(actions, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which) {
                                            case 0:
                                                cm.deleteContactRequest(contact.getUsername());
                                        }
                                    }
                                });
                        builder.create().show();
                    }
                } else {
                    Intent conversationTo = new Intent(getApplicationContext(), MessagesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("CONTACT", contact.getUserID());
                    conversationTo.putExtras(bundle);
                    startActivity(conversationTo);
                }
            }
        });

        mainView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CypherContact contact = adapter.getItem(i);
                String[] actions = new String[]{"Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setTitle(R.string.contact_waiting_actions)
                        .setItems(actions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        cm.blockContact(contact.getUsername());
                                        contactSet.remove(contact);
                                        adapter.notifyDataSetChanged();
                                }
                            }
                        });
                builder.create().show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        contactSet.clear();
        List<CypherContact> list = cm.getContactList();
        Log.d("CONTACTS:", Arrays.toString(list.toArray(new CypherContact[] {})));
        contactSet.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_contact:
                startActivity(new Intent(this, AddContactActivity.class));
                return true;
            case R.id.action_logout:
                Intent login = new Intent(this, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                return  true;
            default:
                return false;
        }
    }

    @Override
    public void onContactChange(CypherContact contact) {
        super.onContactChange(contact);
        switch(contact.getStatus()) {
            case CypherContact.ACCEPTED:
                showToast(R.string.contacts_waiting_action_accepted);
                break;
            case CypherContact.BLOCKED:
                contactView = getLayoutInflater().inflate(R.layout.messages_template, null);
                ImageView contactAvatar = (ImageView) findViewById(R.id.contact_avatar);
                TextView contactName = (TextView) findViewById(R.id.contact_last_time);
                contactAvatar.setImageResource(R.drawable.ic_action_person_blocked);
                contactName.setTextColor(Color.LTGRAY);
                contactView.setClickable(false);
                contactView.setFocusable(false);
                showToast(R.string.contacts_waiting_action_declined);
                break;
            default:
                showToast(R.string.error_general_error);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNewContacts(List<CypherContact> contacts) {
        Log.d("onNewContacts", "CALLED");
        contactSet.addAll(contacts);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onContactDeleted(final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactSet.remove(new CypherContact(name, null));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewMessages(HashMap<Long, List<CypherMessage>> message) {

    }

    @Override
    public void onNewKeys() {

    }

    private class ContactsAdapter extends TreeSetAdapter<CypherContact> {


        public ContactsAdapter(TreeSet<CypherContact> treeSet, CypherContact[] treeSetArray) {
            super(treeSet, treeSetArray);
        }

        @Override
        public long getItemId(int i) {
            return treeSetArray[i].getUserID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CypherContact nowConsidering = getItem(position);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View template = inflater.inflate(R.layout.contacts_template, null);

            TextView contactName = (TextView) template.findViewById(R.id.contact_name);
            TextView contactStatus = (TextView) template.findViewById(R.id.contact_status);
            TextView contactTime = (TextView) template.findViewById(R.id.contact_last_time);

            contactName.setText(nowConsidering.getUsername());

            CypherMessage m = cm.getLastMessage(nowConsidering);
            Time time = new Time();
            if(m != null) {
                time.set(m.getTimestamp());
            } else {
                time.set(nowConsidering.getContactTimestamp());
            }

            if(DateUtils.isToday(time.toMillis(false))) {
                contactTime.setText(time.format("%H.%M"));
            } else {
                contactTime.setText("%d/%m");
            }

            int previewMaxLength = 37;


            switch(nowConsidering.getStatus()) {
                case CypherContact.ACCEPTED:
                    String text = "";
                    if(m != null) {
                        int messLen = m.getText().length();
                        int min = Math.min(previewMaxLength, messLen);
                        String closing = messLen < previewMaxLength ? "" : "...";
                        text = m.getText().substring(0, min) + closing;
                    }
                    contactStatus.setText(text);
                    break;
                case CypherContact.WAITING:
                    contactName.setTextColor(Color.LTGRAY);
                    if(nowConsidering.isFirst()) {
                        contactStatus.setText("Waiting");
                    } else {
                        contactStatus.setText("Action required");
                    }
                    break;
                case CypherContact.BLOCKED:
                    contactName.setTextColor(Color.GRAY);
                    contactName.setPaintFlags(contactName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    contactStatus.setText("You blocked this contact");
                    break;
                case CypherContact.DENIED:
                    contactName.setTextColor(Color.GRAY);
                    contactName.setPaintFlags(contactName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    contactStatus.setText("This contact blocked you");
                    break;
            }

            return template;
        }
    }
}
