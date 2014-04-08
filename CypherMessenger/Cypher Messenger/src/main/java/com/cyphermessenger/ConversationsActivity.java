package com.cyphermessenger;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.ClipData;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.cyphermessenger.sqlite.Contact;
import com.cyphermessenger.sqlite.MySQLiteHelper;

import java.util.List;

public class ConversationsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView chatView = getListView();
        ArrayAdapter<Contact> contact = null;

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_search) {
            View searchView = this.getParent().findViewById(R.layout.activity_conversations);
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
        }
        return super.onOptionsItemSelected(item);
    }

}
