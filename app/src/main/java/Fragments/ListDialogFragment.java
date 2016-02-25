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

    public ListDialogFragment(String title, String[] items, DialogInterface.OnClickListener callback){
        _title = title;
        _items = items;
        _callback = callback;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(_title)
                .setItems(_items, _callback);
        return builder.create();
    }
}
