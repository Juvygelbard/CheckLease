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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import adapters.CustomAdapter;
import bgu_apps.checklease.EditApartment;
import bgu_apps.checklease.R;
import data.Apartment;
import data.Data;
import data.Value;
import db_handle.ApartmentDB;

import android.view.MenuItem;

public class ApartmentListFragment extends Fragment {

    private static ApartmentListFragment _instance;
    private ListView _lv;
    public static ArrayList<Apartment> _apartments;
    private static CustomAdapter _adapter;
    private ApartmentDB _apartmentDB;
    int _longClickedApartment;
    String _pathFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/apartmentsToSend";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public static ApartmentListFragment get_instance(){
        return _instance;
    }

    public void refreshList(){
        _apartments.clear();
        _apartments.addAll(_apartmentDB.getApartmentList());
        _adapter.notifyDataSetChanged();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View layout = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        _instance = this;
        _lv = (ListView)layout.findViewById(R.id.ApartmentList);
        _apartmentDB = ApartmentDB.getInstance();
        _apartments = _apartmentDB.getApartmentList();
        //TODO: here we need to sort!
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
                Intent addApartment = new Intent(ApartmentListFragment.this.getActivity(), EditApartment.class);
                addApartment.putExtra("AppIndex", -1);
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
                Intent editApartment = new Intent(ApartmentListFragment.this.getActivity(), EditApartment.class);
                editApartment.putExtra("AppIndex", _longClickedApartment);
                ApartmentListFragment.this.startActivity(editApartment);
                return true;
            case R.id.sendApartment:
                String filePath = _pathFiles + "/Apartment_" + _apartments.get(_longClickedApartment).getId() + ".wav";
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

    // TODO: WHY STATIC????
    public static void saveFile(File file, Iterator<HashMap.Entry<Integer, Value>> iterator){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                String header = "RIFF"
                        + Integer.toString(0)
                        + "WAVE"
                        + "fmt " +  Long.toString(16)
                        + Integer.toString(16)
                        + Integer.toString(1)
                        + Integer.toString(2)
                        + Long.toString(44100)
                        + Long.toString(88200)
                        + Integer.toString(2)
                        + Integer.toString(16)
                        + "data"
                        + Integer.toString(0);
                header = "RIFF";
                // fos.write(header.getBytes());
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

// Sort the apartments in the list according to their profitability.
    public void sortDefault(){
        Collections.sort(_apartments, new Comparator<Apartment>() {
                    @Override
                    public int compare(Apartment lhs, Apartment rhs) {
                        Float l = (float) lhs.getGivenPrice()/ (float) lhs.getCalcPrice();
                        Float r = (float) rhs.getGivenPrice()/ (float) rhs.getCalcPrice();
                        return l.compareTo(r);
                    }
                }
        );
    }

// Sort the apartments in the list:first by favourites and then according to their profitability.
    public void sortAllByFav(){
        Collections.sort(_apartments, new Comparator<Apartment>() {
            @Override
            public int compare(Apartment lhs, Apartment rhs) {
                Float l = (float) lhs.getGivenPrice() / (float) lhs.getCalcPrice();
                Float r = (float) rhs.getGivenPrice() / (float) rhs.getCalcPrice();
                if (lhs.isFavorite() == rhs.isFavorite())
                    return l.compareTo(r);
                else {
                    if (lhs.isFavorite() && !(rhs.isFavorite()))
                        return 1;
                    else
                        return -1;
                }
            }
        });
    }

    //TODO: decide how we are going to do this...how to show only the favorites (if extra ArrayList or what)
    public void sortOnlyFav(){

    }


//todo: this is not the location of this method! we need to move it when we will preform the getting and reading files of apartments.
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
                for(int i = 0 ; i <= toTheFields.length - 3 ; i = i+3){
                    if(toTheFields[i+1].equals("Integer")){
                        int value = Integer.valueOf(toTheFields[i+2]);
                        apartmentDetails.put(Integer.getInteger(toTheFields[i]) , new Value(value));
                    }
                    else if(toTheFields[i+1].equals("String")){
                        String value = toTheFields[i+2];
                        apartmentDetails.put(Integer.getInteger(toTheFields[i]) , new Value(value));
                    }
                }
                return apartmentDetails;
            } catch (IOException e) {
                e.printStackTrace();
            }
            }catch (FileNotFoundException e) {e.printStackTrace();}
            return apartmentDetails;
        }
}