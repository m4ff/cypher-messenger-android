package com.cyphermessenger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cyphermessenger.sqlite.Contact;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.LinkedList;

public class ContactsActivity extends Activity {

    protected DBManagerAndroidImpl dbManagerAndroid;
    private ListView mainView = null;
    private MenuItem menuItem = null;
    private LinkedList<String> contact = null;
    private ArrayAdapter<String> adapter = null;
    private final ContactsActivity that = this;
    private Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mainView = (ListView) findViewById(R.id.contacts_list);

        contact = new LinkedList<>();
        contact.addLast("Contact n1");
        contact.addLast("Contact n2");
        contact.addLast("Contact n3");
        contact.addLast("Contact n4");
        contact.addLast("Contact n5");
        contact.addLast("Contact n6");
        contact.addLast("Contact n7");
        contact.addLast("Contact n8");
        contact.addLast("Contact n9");
        contact.addLast("Contact n10");
        contact.addLast("Contact n11");
        contact.addLast("Contact n12");
        contact.addLast("Contact n13");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View template = inflater.inflate(R.layout.contacts_template, null);

        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.contacts_template, R.id.contact_last_time, contact);
        mainView.setAdapter(adapter);

        /*contact.addAll( );  TODO ADD QUERY RESULTS*/
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
}
