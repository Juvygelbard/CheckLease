package Fragments;

/**
 * Created by user on 09/01/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;



import java.util.ArrayList;

import adapters.CustomAdapter;
import bgu_apps.checklease.AddApartment;
import bgu_apps.checklease.R;
import data.Apartment;
import data.Data;
import db_handle.ApartmentDB;
import android.view.MenuItem;


public class ApartmentListFragment extends Fragment {

    private ListView _lv;
    private static ArrayList<Apartment> _apartments;
    private CustomAdapter _adapter;
    private ApartmentDB _apartmentDB;

    public ApartmentListFragment(){}

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        _lv = (ListView)layout.findViewById(R.id.ApartmentList);
        registerForContextMenu(_lv);


        //todo: LongClick.
        _lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        _lv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });






            _apartmentDB = ApartmentDB.getInstance();
        dummy();
        _apartments = _apartmentDB.getApartmentList();
        _adapter = new CustomAdapter(_apartments, inflater);
        _lv.setAdapter(_adapter);
        FloatingActionButton fab = (FloatingActionButton)layout.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addApartment = new Intent(ApartmentListFragment.this.getActivity(), AddApartment.class);
                ApartmentListFragment.this.startActivity(addApartment);
            }
        });
        return layout;
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.showApartment:
               // editApartment(info.id);
                return true;
            case R.id.editApartment:
              //  editApartment(info.id);
                return true;
            case R.id.sendApartment:
            //    sendApartment(info.id);
                return true;
            case R.id.deleteApartment:
              //  deleteApartment(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void dummy(){
        Apartment ap1 = new Apartment(0);
        ap1.addValue(Data.FAVORITE, 0);
        ap1.addValue(Data.STREET, "יוסף בן מתתיהו");
        ap1.addValue(Data.BUILDING, 72);
        ap1.addValue(Data.NUM_APARTMENT, 4);
        Apartment ap2 = new Apartment(1);
        ap2.addValue(Data.FAVORITE, 0);
        ap2.addValue(Data.STREET, "קדש");
        ap2.addValue(Data.BUILDING, 72);
        ap2.addValue(Data.NUM_APARTMENT, 4);
        Apartment ap3 = new Apartment(2);
        ap3.addValue(Data.FAVORITE, 0);
        ap3.addValue(Data.STREET, "סימטת הבשור");
        ap3.addValue(Data.BUILDING, 72);
        ap3.addValue(Data.NUM_APARTMENT, 4);
        Apartment ap4 = new Apartment(3);
        ap4.addValue(Data.FAVORITE, 0);
        ap4.addValue(Data.STREET, "וינגייט");
        ap4.addValue(Data.BUILDING, 72);
        ap4.addValue(Data.NUM_APARTMENT, 4);

        _apartmentDB.addApartment(ap1);
        _apartmentDB.addApartment(ap2);
        _apartmentDB.addApartment(ap3);
        _apartmentDB.addApartment(ap4);
    }
}