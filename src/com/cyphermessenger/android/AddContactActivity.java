package com.cyphermessenger.android;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.support.v7.widget.SearchView;
import com.cyphermessenger.client.BasicContentListener;
import com.cyphermessenger.client.ContentManager;
import com.cyphermessenger.client.CypherContact;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.LinkedList;
import java.util.List;

public class AddContactActivity extends MainActivity {

    private final List<String> contacts = new LinkedList<>();
    private ProgressBar progressBar;
    private ArrayAdapter<String> contactsAdapter;
    private boolean lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View template = inflater.inflate(R.layout.add_contact_template, null);

        final AddContactActivity that = this;
        progressBar = (ProgressBar) findViewById(R.id.add_contact_progress);
        contactsAdapter = new ArrayAdapter<>(this, R.layout.add_contact_template, R.id.add_contact_item, contacts);
        ListView addContactList = (ListView) findViewById(R.id.add_contact_list);
        addContactList.setAdapter(contactsAdapter);
        addContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!lock) {
                    new MyDialogFragment(contacts.get(position), that).show(getSupportFragmentManager(), "DIALOG");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.add_contact_search_view);
        MenuItemCompat.expandActionView(menuItem);
        final SearchView searchContactsView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchContactsView.setIconifiedByDefault(true);
        searchContactsView.setIconified(false);
        searchContactsView.setFocusable(true);
        searchContactsView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() >= 4) {
                    doRequest(s);
                }
                return true;
            }

            private void doRequest(String s) {
                progressBar.setVisibility(View.VISIBLE);
                cm.findUser(s);
            }
        });
        searchContactsView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchContactsView.setIconified(false);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void handleOk(String name) {
        if(!lock) {
            lock = true;
            contacts.remove(name);
            contactsAdapter.notifyDataSetChanged();
            cm.addContact(name);
        }
        lock = false;
    }

    @Override
    public void onContactChange(final CypherContact contact) {
        finish();
        showToast(R.string.alert_text);
    }

    @Override
    public void onContactDenied() {
        showToast(R.string.error_contact_denied);
    }

    @Override
    public void onUsernameNotFound() {
        showToast(R.string.error_contact_not_found);
    }

    @Override
    public void onFindUser(List<String> list) {
        contacts.clear();
        list.remove(cm.getSession().getUser().getUsername());
        contacts.addAll(list);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}