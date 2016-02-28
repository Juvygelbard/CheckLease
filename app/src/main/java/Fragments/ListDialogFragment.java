package Fragments;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Juvy on 24/02/2016.
 */
public class ListDialogFragment extends DialogFragment {
    private String _title;
    private String[] _items;
    private DialogInterface.OnClickListener _callback;
    private int _choice;

    public ListDialogFragment(String title, String[] items, DialogInterface.OnClickListener callback, int choice){
        _title = title;
        _items = items;
        _callback = callback;
        _choice = choice;
    }

    public ListDialogFragment(String title, String[] items, DialogInterface.OnClickListener callback){
        this(title, items, callback, -1);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(_title);
        if(_choice>=0)
            builder.setSingleChoiceItems(_items, _choice, _callback);
        else
            builder.setItems(_items, _callback);
        return builder.create();
    }
}
