package com.cyphermessenger;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cyphermessenger.sqlite.Messages;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class MessagesActivity extends ActionBarActivity {

    LinkedList<Messages> messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        ListView messageListView = (ListView) findViewById(R.id.messages_main_view);
        messagesList = new LinkedList<Messages>();
        messagesList.addLast(new Messages(1,"ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(2,"ciao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao ciao ciao ciao", (int) System.currentTimeMillis(), true, false));

        MyAdapter adapter = new MyAdapter();
        messageListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
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

    private class MyAdapter extends ArrayAdapter<Messages> {

        public MyAdapter() {
            super(getApplicationContext(), R.layout.messages_template, messagesList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Messages nowConsidering = (Messages) getItem(position);

            View template = convertView;
            if(template == null) {
                LayoutInflater inflater = getLayoutInflater();
                template = inflater.inflate(R.layout.messages_template, parent, false);
            }
            TextView messageDisplaying = (TextView) findViewById(R.id.message_bubble);
            if(nowConsidering.isUserSender()) {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_user);
                messageDisplaying.setText(nowConsidering.getText());
                messageDisplaying.setGravity(Gravity.LEFT);
            } else {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_contat);
                messageDisplaying.setText(nowConsidering.getText());
                messageDisplaying.setGravity(Gravity.RIGHT);
            }

            return convertView;
        }
    }

}
