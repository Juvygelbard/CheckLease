package Fragments;

/**
 * Created by user on 09/01/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;

import adapters.CustomAdapter;
import bgu_apps.checklease.AddApartment;
import bgu_apps.checklease.R;
import data.Apartment;
import data.Data;
import db_handle.ApartmentDB;

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

    public void dummy(){
        Apartment ap1 = new Apartment();
        ap1.add(Data.FAVORITE, 0);
        ap1.add(Data.STREET, "יוסף בן מתתיהו");
        ap1.add(Data.BUILDING, 72);
        ap1.add(Data.NUM_APARTMENT, 4);
        Apartment ap2 = new Apartment();
        ap2.add(Data.FAVORITE, 0);
        ap2.add(Data.STREET, "קדש");
        ap2.add(Data.BUILDING, 72);
        ap2.add(Data.NUM_APARTMENT, 4);
        Apartment ap3 = new Apartment();
        ap3.add(Data.FAVORITE, 0);
        ap3.add(Data.STREET,"סימטת הבשור");
        ap3.add(Data.BUILDING, 72);
        ap3.add(Data.NUM_APARTMENT, 4);
        Apartment ap4 = new Apartment();
        ap4.add(Data.FAVORITE, 0);
        ap4.add(Data.STREET,"וינגייט" );
        ap4.add(Data.BUILDING, 72);
        ap4.add(Data.NUM_APARTMENT, 4);

        _apartmentDB.addApartment(ap1);
        _apartmentDB.addApartment(ap2);
        _apartmentDB.addApartment(ap3);
        _apartmentDB.addApartment(ap4);
    }
}