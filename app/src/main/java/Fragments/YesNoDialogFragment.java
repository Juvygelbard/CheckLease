package Fragments;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

/**
 * Created by Juvy on 25/02/2016.
 */
public class YesNoDialogFragment extends DialogFragment {
    private String _message;
    private DialogInterface.OnClickListener _yesListener;
    private DialogInterface.OnClickListener _noListener;

    public YesNoDialogFragment(String message, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener){
        _message = message;
        _yesListener = yesListener;
        _noListener = noListener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(_message)
                .setPositiveButton("כן", _yesListener)
                .setNegativeButton("לא", _noListener);
        return builder.create();
        }
}
