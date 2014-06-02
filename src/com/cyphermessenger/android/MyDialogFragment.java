package com.cyphermessenger.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Pier DAgostino on 15/05/2014.
 */
public class MyDialogFragment extends DialogFragment {

    final String contactName;
    final AddContactActivity caller;

    public MyDialogFragment(String contactName, final AddContactActivity caller) {
        this.contactName = contactName;
        this.caller = caller;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caller);
        builder.setTitle(R.string.alert_title);
        builder.setMessage("Would you like to add " + contactName + " to your contacts?");
        builder.setPositiveButton(R.string.alert_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                caller.onBackPressed();
                caller.handleOk(contactName);
            }
        });
        builder.setNegativeButton(R.string.alert_button_cancel, null);
        return builder.create();
    }
}