package Fragments;

/**
 * Created by user on 09/01/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import adapters.CustomAdapter;
import bgu_apps.checklease.AddApartment;
import bgu_apps.checklease.R;
import data.Apartment;
import data.Data;
import data.Value;
import db_handle.ApartmentDB;
import android.view.MenuItem;



public class ApartmentListFragment extends Fragment {

    private ListView _lv;
    private static ArrayList<Apartment> _apartments;
    private CustomAdapter _adapter;
    private ApartmentDB _apartmentDB;
    int _longClickedApartment;
    String _pathFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/apartmentsToSend";

    public ApartmentListFragment(){}

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View layout = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        _lv = (ListView)layout.findViewById(R.id.ApartmentList);
        _apartmentDB = ApartmentDB.getInstance();
        dummy();
        _apartments = _apartmentDB.getApartmentList();
        _adapter = new CustomAdapter(_apartments, inflater);
        _lv.setAdapter(_adapter);
        this.registerForContextMenu(_lv);

        _lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                _longClickedApartment = position;
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton)layout.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addApartment = new Intent(ApartmentListFragment.this.getActivity(), AddApartment.class);
                ApartmentListFragment.this.startActivity(addApartment);
            }
        });

        File dir = new File(_pathFiles);
        dir.mkdirs();

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
            case R.id.editApartment:
                return true;
            case R.id.sendApartment:
                String filePath = _pathFiles + "/Apartment_" + _apartments.get(_longClickedApartment).getId() + ".clt";
                File file = new File(filePath);
                saveFile(file,_apartments.get(_longClickedApartment).getFeatureIterator());
                Uri fileUri = Uri.parse(filePath);
                Intent msgIntent = new Intent();
                msgIntent.setAction(Intent.ACTION_SEND);
                msgIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                msgIntent.setType("*/*");
                this.getActivity().startActivity(msgIntent);
                return true;
            case R.id.callApartment:
                return true;
            case R.id.deleteApartment:
                Apartment currApartment = _apartments.get(_longClickedApartment);
                ApartmentDB apartmentDB = ApartmentDB.getInstance();
                apartmentDB.deleteApartment(currApartment.getId());
                _apartments.remove(_longClickedApartment);
                _adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static void saveFile(File file, Iterator<HashMap.Entry<Integer, Value>> iterator){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                while (iterator.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) iterator.next();
                    Value v = (Value) pair.getValue();
                    String value = v.toString();
                    String key = pair.getKey().toString();
                    fos.write(key.getBytes());
                    fos.write(Data.LINE_SEPARATOR.getBytes());
                    fos.write(value.getBytes());
                    fos.write(Data.LINE_SEPARATOR.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
    }



//todo: this is not the location of this method! we need to move it when we will preform the getting and reading files of apartments.
//todo: complete: need to insert from String[] toTheFields to the HashMap apartmentDetails. make sure how we make a different between the files that are int an the String ones.
    public static HashMap<Integer, Value> loadFile(File file){
        HashMap<Integer, Value> apartmentDetails = new HashMap<Integer, Value>();
        try {
            FileInputStream fis = new FileInputStream(file);
            try {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String fromFile = br.readLine();
                while (fromFile != null) {
                    sb.append(fromFile);
                    fromFile = br.readLine();
                }
                fromFile = sb.toString();
                String[] toTheFields = fromFile.split(Data.LINE_SEPARATOR);

                //to complete!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                return apartmentDetails;
            } catch (IOException e) {
                e.printStackTrace();
            }
            }catch (FileNotFoundException e) {e.printStackTrace();}
            return apartmentDetails;
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