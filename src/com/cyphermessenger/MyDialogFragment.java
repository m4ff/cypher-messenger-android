package com.cyphermessenger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pier DAgostino on 15/05/2014.
 */
public class MyDialogFragment extends DialogFragment {

    String contactName;
    AddContactActivity caller;

    public MyDialogFragment(String contactName, final AddContactActivity caller) {
        this.contactName = contactName;
        this.caller = caller;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caller);
        builder.setTitle(R.string.alert_title);
        builder.setMessage("Would you like to add " + contactName + " to your contact list?");
        builder.setPositiveButton(R.string.alert_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                caller.handleContact();

            }
        });
        builder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

}