package com.cyphermessenger;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cyphermessenger.sqlite.Contact;

import java.util.LinkedList;

public class ConversationsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        ListView mainView = (ListView) findViewById(R.id.conversations_list);
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

        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, R.layout.conversation_tamplate, R.id.conversation_last_time, contact);
        mainView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversations, menu);
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
