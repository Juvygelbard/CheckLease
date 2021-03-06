package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import bgu_apps.checklease.MainActivity;
import data.Apartment;
import bgu_apps.checklease.R;
import data.Data;
import db_handle.ApartmentDB;

/**
 * Created by user on 30/12/2015.
 */
public class CustomAdapter extends BaseAdapter {

    private LayoutInflater _inflater;
    private ArrayList<Apartment> _allApartments;

    public CustomAdapter(ArrayList<Apartment> allApartments, LayoutInflater inflater){
        _allApartments = allApartments;
        _inflater = inflater;
    }

    @Override
    public int getCount() {
        return _allApartments.size();
    }

    @Override
    public Object getItem(int position) {
        return _allApartments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = _inflater.inflate(R.layout.row_apartment, parent, false );

        TextView address = (TextView) row.findViewById(R.id.address);
        ImageView rateImg = (ImageView) row.findViewById(R.id.image_rate);
        final ImageButton favoriteImg = (ImageButton) row.findViewById(R.id.button_favorite);
        favoriteImg.setFocusable(false);

        final Apartment curr = _allApartments.get(position);

        address.setText(curr.getAddress());
        int rate = Data.getRate(curr.getCalcPrice(), curr.getGivenPrice());
        // determine color
        switch(rate){
            case 0:
                rateImg.setImageResource(R.drawable.home_grey);
                break;
            case 1:
                rateImg.setImageResource(R.drawable.home_green);
                break;
            case 2:
                rateImg.setImageResource(R.drawable.home_orange);
                break;
            case 3:
                rateImg.setImageResource(R.drawable.home_red);
                break;
        }
        if(curr.isFavorite())
            favoriteImg.setImageResource(R.drawable.star_full_orange);
        else
            favoriteImg.setImageResource(R.drawable.star_empty_grey);

        favoriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApartmentDB apartmentDB = ApartmentDB.getInstance();
                if(curr.isFavorite()) {
                    favoriteImg.setImageResource(R.drawable.star_empty_grey);
                    curr.setFavorite(false);
                    apartmentDB.setFavorite(curr.getId(), false);
                }
                else {
                    favoriteImg.setImageResource(R.drawable.star_full_orange);
                    curr.setFavorite(true);
                    apartmentDB.setFavorite(curr.getId(), true);
                }
                MainActivity._favListFragment.refreshList();
                if(MainActivity.getCurrTabIndex() == 1 || Data.getSortBy() == Data.SORT_FAV);
                    MainActivity._fullListFragment.refreshList();
                MainActivity._mapFragment.refreshMap();
            }
        });

        return row;
    }
}