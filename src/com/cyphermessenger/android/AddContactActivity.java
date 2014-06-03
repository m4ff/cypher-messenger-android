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
import android.support.v4.app.FragmentActivity;
import com.cyphermessenger.client.BasicContentListener;
import com.cyphermessenger.client.ContentManager;
import com.cyphermessenger.client.CypherContact;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.LinkedList;
import java.util.List;

public class AddContactActivity extends FragmentActivity {

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

            private void doRequest(final String s) {
                progressBar.setVisibility(View.VISIBLE);
                final ContentManager contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(getApplicationContext()));
                contentManager.setContentListener(new BasicContentListener() {
                    @Override
                    public void onFindUser(List<String> list) {
                        contacts.clear();
                        list.remove(contentManager.getSession().getUser().getUsername());
                        contacts.addAll(list);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contactsAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                contentManager.findUser(s);
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
            ContentManager cm = new ContentManager(DBManagerAndroidImpl.getInstance(this));
            cm.setContentListener(new BasicContentListener() {
                @Override
                public void onContactChange(final CypherContact contact) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            AUtils.shortToast(R.string.alert_text, getApplicationContext());
                        }
                    });
                }

                @Override
                public void onContactDenied() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AUtils.shortToast(R.string.error_contact_denied, getApplicationContext());
                        }
                    });
                }

                @Override
                public void onUsernameNotFound() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AUtils.shortToast(R.string.error_contact_not_found, getApplicationContext());
                        }
                    });
                }

                @Override
                public void onServerError() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AUtils.shortToast(R.string.error_general_error, getApplicationContext());
                        }
                    });
                }
            });
            contacts.remove(name);
            contactsAdapter.notifyDataSetChanged();
            cm.addContact(name);
        }
        lock = false;
    }
}