package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bgu_apps.checklease.R;

/**
 * Created by user on 20/02/2016.
 */
public class SettingAdapter extends BaseAdapter {

    private LayoutInflater _inflater;
    private ArrayList<String> _allItems;

    public SettingAdapter(LayoutInflater inflater){
        _allItems = new ArrayList<String>();
        _allItems.add(0, "עיר מגורים");
        _allItems.add(1, "מיון הדירות");
        _allItems.add(2, "שיתוף נתוני סטטיסטיקה בלבד");
        _allItems.add(3, "שחזור דירות שנמחקו");
        _allItems.add(4, "מחיקת כל הדירות");
        _allItems.add(5, "");
        _inflater = inflater;
    }

    @Override
    public int getCount() {
        return _allItems.size();
    }

    @Override
    public Object getItem(int position) {
        return _allItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {




        View row = _inflater.inflate(R.layout.row_settings, parent, false );
        TextView tv = (TextView) row.findViewById(R.id.item_settings);

        tv.setText(_allItems.get(position));

        return row;
    }
}