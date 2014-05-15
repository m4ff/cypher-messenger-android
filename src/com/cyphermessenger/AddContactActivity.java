package com.cyphermessenger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.Inflater;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.*;

public class AddContactActivity extends Activity {

	LinkedList<String> contacts = new LinkedList<>();
    ListView addContactList = null;
    TextView contact = null;
    ArrayAdapter<String> contactsAdapter = null;
    SearchView searchContact = null;
    MenuItem menuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View template = inflater.inflate(R.layout.add_contact_template, null);
        contact = (TextView) template.findViewById(R.id.add_contact_item);
        addContactList = (ListView) findViewById(R.id.add_contact_list);
        contactsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.add_contact_template, R.id.add_contact_item, contacts);



        handleIntent(getIntent());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contact_search_view:
                searchContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;
            default:
                return false;
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            contacts.addLast("query eseguita");
            addContactList.setAdapter(contactsAdapter);

            addContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.d("DEBUG:", "in realtà ti ho tappato");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage(R.string.alert_text);
                    builder.setPositiveButton(R.string.alert_button_add, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast added = new Toast(getApplicationContext());
                            added.setText("Your contact is now aviable on your list");
                        }
                    });
                    builder.setNegativeButton(R.string.alert_button_cancel, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO THINK ABOUT LATER
                        }
                    });

                    builder.create();
                }
            });
            menuItem.collapseActionView();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            menuItem = menu.findItem(R.id.add_contact_search_view);
            searchContact = (SearchView) menuItem.getActionView();
            searchContact.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            menuItem.expandActionView();
            searchContact.setIconifiedByDefault(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}