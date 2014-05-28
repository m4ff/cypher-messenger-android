package com.cyphermessenger.android;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cyphermessenger.sqlite.Messages;

import java.util.LinkedList;
import java.util.List;


public class MessagesActivity extends ActionBarActivity {

    LinkedList<Messages> messagesList;
    ListView messageListView = null;
    int message_id = 26;
    boolean is_User = false;
    MyAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        messageListView = (ListView) findViewById(R.id.messages_list_view);
        messagesList = new LinkedList<Messages>();
        messagesList.addLast(new Messages(1,"ciao ciao fvtvtfbt  fvtgvyhvyghvgv ftvbybbtvtv fvygvtfcvtfc tfytggyuhiygy iuh7g6r", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(2,"ciao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(3,"ciao ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(4,"jhscbvanblnvqbdb absalblvblasbvk", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(5,"smcvkjanbkc bnamk a", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(6,"nasjkonvnackjvnakfdnvipjn sdvnahisbvd", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(7,"lkanpv a npnansdjvnakjm njasndvjansvjna", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(8,"ciaaadfbadfba asda asgwgnas", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(9,"casfg afg qgsdgsbh", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(10,"sdfb agg qrasfgsdfb", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(11,"sdfhbs afga asfgasgsdbaxc", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(12,"sdfbnsdf basdfvbac", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(13,"asdvbahfbva jhbahvbasdv", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(14,"nsjvakvn kons asdca", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(15,"ciaoadv adfb asfdb f", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(16,"ciao ciao ciao ciao asdfba dfgbas a", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(17,"ciao ciao ciaoadf a aa fasdfg dfgsdbgaf ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(18,"ciao csdfhbw sbs ynwbscvbsdfgb iao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(19,"ciao ciasdfgh wtwethw thwo ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(20,"ciao ciao ciao sd gswtgh sth tciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(21,"ciaosd vhbwsd hrt sbnws  ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(22,"ciao cisdh sdgh sg ao ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(23,"ciao dfgh sgh fgh wrthhnghmn ciao ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(24,"ciao ciadfhnjd f dfghdf o ciao ciao", (int) System.currentTimeMillis(), true, false));
        messagesList.addLast(new Messages(25,"ciao ciaodfg hsth  hjgmdghjdt ciao ciao", (int) System.currentTimeMillis(), true, true));
        messagesList.addLast(new Messages(26,"ciao ciaodfhjdfhmjdfhj ciao ciao", (int) System.currentTimeMillis(), true, false));

        adapter = new MyAdapter(this, R.id.message_bubble, messagesList);
        messageListView.setAdapter(adapter);

        final EditText messageEditor = (EditText) findViewById(R.id.message_editor);
        ImageButton buttonSend = (ImageButton) findViewById(R.id.message_button_send);

        messageEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesList.addLast(new Messages(message_id,messageEditor.getText().toString(), (int) System.currentTimeMillis(), true, !is_User));
                messageListView.post(new Runnable() {
                    @Override
                    public void run() {
                        messageListView.setSelection(adapter.getCount() - 1);
                    }
                });
                messageEditor.setText("");
                messageEditor.setHint(R.string.prompt_text_message_hint);
                is_User = !is_User;
                message_id++;
            }
        });

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
