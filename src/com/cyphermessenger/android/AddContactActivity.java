package com.cyphermessenger.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cyphermessenger.R;

import java.util.LinkedList;

public class AddContactActivity extends Activity {

	private LinkedList<String> contacts = new LinkedList<>();
    private ListView addContactList = null;
    private TextView contact = null;
    private ArrayAdapter<String> contactsAdapter = null;
    private SearchView searchContact = null;
    private MenuItem menuItem = null;
    private Menu menu = null;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact, menu);
        this.menu = menu;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            menuItem = menu.findItem(R.id.add_contact_search_view);
            searchContact = (SearchView) menuItem.getActionView();
            searchContact.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            menuItem.expandActionView();
            searchContact.setIconifiedByDefault(true);
        }
        return super.onCreateOptionsMenu(menu);
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
            case R.id.home:
                NavUtils.navigateUpTo(getParent(),getIntent());
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
    private void handleIntent(final Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            contacts.addLast(query);
            contacts.addLast("pippo91");
            contacts.addLast("pippo74");
            contacts.addLast("pippo53");
            contacts.addLast("pippoGoofy");
            contacts.addLast("pippo90");
            contacts.addLast("pippoFoo");
            addContactList.setAdapter(contactsAdapter);

            final AddContactActivity that = this;

            addContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String contactName = (String) adapterView.getItemAtPosition(position);
                    MyDialogFragment dialogFragment = new MyDialogFragment(contactName, that);
                    dialogFragment.show(getFragmentManager(), "DIALOG");
                }
            });
            menuItem.collapseActionView();

        }
    }


    public void handleContact() {
        // TODO SOME WORK
        Toast t = new Toast(getApplicationContext());
        t.makeText(getApplicationContext(), R.string.alert_text, Toast.LENGTH_LONG).show();
        finishActivity(RESULT_OK);
    }
}