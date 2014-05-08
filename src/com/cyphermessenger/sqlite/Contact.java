package com.cyphermessenger.sqlite;


import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class Contact {

    private String contactName;
    private int avatarId;

    public Contact(String contactName, int avatarId) {
        this.contactName = contactName;
        this.avatarId = avatarId;
    }

    // GETTERS
    public String getName() {
        return contactName;
    }

    public int getAvatar() {
        return avatarId;
    }

    public void setAvatar(ImageView view) {
        view.setImageDrawable(Drawable.createFromPath("C:\\Users\\Pier D'Agostino\\Documents\\GitHub\\cypher-messenger-android\\CypherMessenger\\Cypher Messenger\\src\\main\\java\\com\\cyphermessenger\\avatars\\" + avatarId + ".jpg"));
    }
}
