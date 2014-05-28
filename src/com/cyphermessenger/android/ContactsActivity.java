package com.cyphermessenger.android;

import android.app.Activity;
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
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContactsActivity extends Activity implements NotificationListener {

    private ListView mainView;
    private List<CypherContact> contact = new LinkedList<>();
    private ArrayAdapter<CypherContact> adapter;
    private final ContactsActivity that = this;

    private MenuItem menuItem;
    private Menu menu;

    private ContentUpdateManager updateManager;
    private ContentManager contentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(this));
        updateManager = new ContentUpdateManager(this, this);

        mainView = (ListView) findViewById(R.id.contacts_list);

        contact = contentManager.getContactList();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View template = inflater.inflate(R.layout.contacts_template, null);

        adapter = new ArrayAdapter<>(this, R.layout.contacts_template, R.id.contact_last_time, contact);
        mainView.setAdapter(adapter);

        mainView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent conversationTo = new Intent(that, MessagesActivity.class);
                conversationTo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                that.startActivity(conversationTo);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateManager.setShortInterval();
        contact.clear();
        contact.addAll(contentManager.getContactList());
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateManager.setLongInterval();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_contact:
                Intent addContactIntent = new Intent(this, AddContactActivity.class);
                this.startActivityForResult(addContactIntent, Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            default:
                return false;
        }
    }

    @Override
    public void onNewContacts(List<CypherContact> contacts) {
        contact.addAll(contacts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNewMessages(HashMap<Long, List<CypherMessage>> message) {

    }

    @Override
    public void onNewKeys() {

    }
}
