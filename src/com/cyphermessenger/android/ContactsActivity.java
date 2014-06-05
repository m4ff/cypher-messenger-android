package com.cyphermessenger.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cyphermessenger.client.*;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContactsActivity extends MainActivity {

    private ListView mainView;
    private final List<CypherContact> contactList = new LinkedList<>();
    private ArrayAdapter<CypherContact> adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mainView = (ListView) findViewById(R.id.contacts_list);

        contactList.addAll(cm.getContactList());

        adapter = new ArrayAdapter<>(this, R.layout.contacts_template, R.id.contact_last_time, contactList);
        mainView.setAdapter(adapter);

        mainView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CypherContact contact = adapter.getItem(position);
                Intent conversationTo = new Intent(getApplicationContext(), MessagesActivity.class);
                //conversationTo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putLong("CONTACT", contact.getUserID());
                conversationTo.putExtras(bundle);
                startActivity(conversationTo);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactList.clear();
        contactList.addAll(cm.getContactList());
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
                startActivity(new Intent(this, LoginActivity.class));
                return  true;
            default:
                return false;
        }
    }

    @Override
    public void onNewContacts(List<CypherContact> contacts) {
        Log.d("onNewContacts", "CALLED");
        contactList.addAll(contacts);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
}
