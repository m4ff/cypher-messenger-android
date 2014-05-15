package com.cyphermessenger;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cyphermessenger.sqlite.Contact;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.LinkedList;

public class ContactsActivity extends ActionBarActivity {

    protected DBManagerAndroidImpl dbManagerAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ListView mainView = (ListView) findViewById(R.id.contacts_list);

        LinkedList<Contact> contact = new LinkedList<Contact>();
        contact.addFirst(new Contact("cadcsd", 1));
        contact.addFirst(new Contact("cadgsfdfsd", 2));
        contact.addFirst(new Contact("ctgerverer", 3));
        contact.addFirst(new Contact("jdfvbksdf", 4));
        contact.addFirst(new Contact("ctgvsadfsad", 5));
        contact.addFirst(new Contact("casdasfvsd", 6));
        contact.addFirst(new Contact("yrhdftfwe", 7));
        contact.addFirst(new Contact("ujtftdgwse", 8));
        contact.addFirst(new Contact("bfnbdseawec", 9));
        contact.addFirst(new Contact("cytehsdgacr", 10));
        contact.addFirst(new Contact("crujbdtvgcwex", 11));
        contact.addFirst(new Contact("sdgxjdv", 12));
        contact.addFirst(new Contact("yrilouibfdhsgd", 13));
        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, R.layout.contacts_template, R.id.contact_last_time, contact);

        mainView.setAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contacts, menu);
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

}
