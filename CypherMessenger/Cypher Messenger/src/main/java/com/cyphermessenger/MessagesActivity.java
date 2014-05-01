package com.cyphermessenger;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cyphermessenger.sqlite.Messages;

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
        messagesList.addLast(new Messages(1,"ciao ciao fvtvtfbt  fvtgvyhvyghvgv ftvbybbtvtv fvygvtfcvtfc tfytggyuhiygy iuh7g6r", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(2,"ciao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"jhscbvanblnvqbdb absalblvblasbvk", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"smcvkjanbkc bnamk a", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"nasjkonvnackjvnakfdnvipjn sdvnahisbvd", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"lkanpv a npnansdjvnakjm njasndvjansvjna", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciaaadfbadfba asda asgwgnas", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"casfg afg qgsdgsbh", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"sdfb agg qrasfgsdfb", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"sdfhbs afga asfgasgsdbaxc", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"sdfbnsdf basdfvbac", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"asdvbahfbva jhbahvbasdv", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"nsjvakvn kons asdca", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciaoadv adfb asfdb f", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao ciao ciao ciao asdfba dfgbas a", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciao ciaoadf a aa fasdfg dfgsdbgaf ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao csdfhbw sbs ynwbscvbsdfgb iao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciasdfgh wtwethw thwo ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao ciao ciao sd gswtgh sth tciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciaosd vhbwsd hrt sbnws  ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao cisdh sdgh sg ao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao dfgh sgh fgh wrthhnghmn ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao ciadfhnjd f dfghdf o ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciaodfg hsth  hjgmdghjdt ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"ciao ciaodfhjdfhmjdfhj ciao ciao", (int) System.currentTimeMillis(), true, false));

        messageListView.setAdapter(new MyAdapter(this, R.id.message_bubble, messagesList));

        EditText messageEditor = (EditText) findViewById(R.id.message_editor);
        messageEditor.setImeOptions(EditorInfo.IME_ACTION_SEND);
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

        public MyAdapter(Context context, int textViewResourceId, List<Messages> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Messages nowConsidering = getItem(position);


            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View template = inflater.inflate(R.layout.messages_template, null);

            int width = parent.getWidth() - 100;

            LinearLayout messageDisplayingLayout = (LinearLayout) template.findViewById(R.id.message_bubble);
            TextView messageDisplaying = (TextView) template.findViewById(R.id.message_bubble_text);
            messageDisplaying.setText(nowConsidering.getText());

            if(nowConsidering.isUserSender()) {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_user);
                messageDisplayingLayout.setGravity(Gravity.RIGHT);
                messageDisplayingLayout.setPadding(dpToPx(60), 0, 0, 0);
            } else {
                messageDisplaying.setBackgroundResource(R.drawable.message_bubble_contat);
                messageDisplayingLayout.setGravity(Gravity.LEFT);
                messageDisplayingLayout.setPadding(0, 0, dpToPx(60), 0);
            }

            return template;
        }

        public int dpToPx(int dp) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return px;
        }
    }

}
