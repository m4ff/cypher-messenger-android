package com.cyphermessenger.android;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cyphermessenger.R;


import java.util.Iterator;
import java.util.LinkedList;

public class ConversationsActivity extends Activity {

    SearchView searchView = null;
    MenuItem menuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        ListView mainView = (ListView) findViewById(R.id.conversations_list);
        LinkedList<String> contact = new LinkedList<>();
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
        View template = inflater.inflate(R.layout.conversation_template, null);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.conversation_template, R.id.conversation_last_time, contact);
        mainView.setAdapter(adapter);

        LinearLayout gridLayout = (LinearLayout) template.findViewById(R.id.conversation_grid);
        gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversations, menu);if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            menuItem = menu.findItem(R.id.action_search_conversations);
            this.getActionBar().setDisplayShowHomeEnabled(true);
            searchView = (SearchView) menuItem.getActionView();
            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_search) {
            final String[] chatToFind = {""};
            final EditText searchEditor = new EditText(this.getApplicationContext());
            searchEditor.setHint("Search a conversation");
            searchEditor.setInputType(InputType.TYPE_CLASS_TEXT);
            searchEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        chatToFind[0] = String.valueOf(searchEditor.getText());
                    } else {
                        searchEditor.setHint("Search a conversation");
                    }
                }
            });
        }*/
        return super.onOptionsItemSelected(item);
    }

}
