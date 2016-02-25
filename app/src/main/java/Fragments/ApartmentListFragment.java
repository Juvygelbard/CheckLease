package Fragments;

/**
 * Created by user on 09/01/2016.
 */

import android.content.DialogInterface;
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
import bgu_apps.checklease.MainActivity;
import bgu_apps.checklease.R;
import bgu_apps.checklease.ShowApartment;
import data.Apartment;
import data.Data;
import data.Value;
import db_handle.ApartmentDB;
import android.view.MenuItem;
import android.widget.Toast;

public class ApartmentListFragment extends Fragment {

    private ListView _lv;
    public static ArrayList<Apartment> _apartments;
    public static ArrayList<Apartment> _apartmentsFavs;
    private CustomAdapter _adapter;
    private ApartmentDB _apartmentDB;
    int _longClickedApartment;
    String _pathFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/apartmentsToSend";
    private boolean _isFav;

    public ApartmentListFragment(){
        if(MainActivity._currTab == 0)
            _isFav = false;
        else
            _isFav = true;
    }
    public ApartmentListFragment(boolean isFav){
        _isFav = isFav;
    }

    public void refreshList(){
        if(_isFav) {
            _apartmentsFavs.clear();
            _apartmentsFavs.addAll(_apartmentDB.getFavoriteList());
        }
        else {
            _apartments.clear();
            _apartments.addAll(_apartmentDB.getApartmentList());
        }
        _adapter.notifyDataSetChanged();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View layout = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        _lv = (ListView)layout.findViewById(R.id.ApartmentList);
        _apartmentDB = ApartmentDB.getInstance();
        if(_isFav) {
            _apartmentsFavs = _apartmentDB.getFavoriteList();
            _adapter = new CustomAdapter(_apartmentsFavs, inflater);
        }
        else {
            _apartments = _apartmentDB.getApartmentList();
            _adapter = new CustomAdapter(_apartments, inflater);
        }
        //TODO: here we need to sort!

        _lv.setAdapter(_adapter);
        this.registerForContextMenu(_lv);

        _lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                _longClickedApartment = position;
                return false;
            }
        });
        _lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showApartment = new Intent(ApartmentListFragment.this.getActivity(), ShowApartment.class);
                showApartment.putExtra("AppIndex", position);
                ApartmentListFragment.this.startActivity(showApartment);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)layout.findViewById(R.id.add_button);
        if(!_isFav) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addApartment = new Intent(ApartmentListFragment.this.getActivity(), EditApartment.class);
                    addApartment.putExtra("AppIndex", -1);
                    ApartmentListFragment.this.startActivity(addApartment);
                }
            });
        }
        else
            fab.setVisibility(View.GONE);


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
                ArrayList<String> options = Data.getPhonefieldList();
                switch(options.size()){
                    case 0: // no phone options in list.
                        Toast msg = Toast.makeText(this.getActivity().getApplicationContext(),"לא קיים מספר טלפון בפרטי הדירה!", Toast.LENGTH_SHORT);
                        msg.show();
                        break;
                    case 1: // only one phone option in list.
                        int id = Data.getPhonefieldID(options.get(0));
                        String phone = _apartments.get(_longClickedApartment).getValue(id).getStrValue();
                        Intent dial = new Intent(Intent.ACTION_DIAL);
                        dial.setData(Uri.parse("tel:" + phone));
                        this.startActivity(dial);
                        break;
                    default: // more than one phone option in list.
                        final String[] phoneList = options.toArray(new String[options.size()]);
                        ListDialogFragment choosePhone = new ListDialogFragment("למי להתקשר?", phoneList, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int id = Data.getPhonefieldID(phoneList[which]);
                                String phone = _apartments.get(_longClickedApartment).getValue(id).getStrValue();
                                Intent dial = new Intent(Intent.ACTION_DIAL);
                                dial.setData(Uri.parse("tel:" + phone));
                                ApartmentListFragment.this.startActivity(dial);
                            }
                        });
                        choosePhone.show(getFragmentManager(), "רשימת טלפונים");
                        break;
                }
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
                while (iterator.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) iterator.next();
                    Value v = (Value) pair.getValue();
                    String str_val = v.getStrValue();
                    String int_val = "" + v.getIntValue();
                    String key = pair.getKey().toString();
                    fos.write(key.getBytes());
                    fos.write(Data.VAL_SEPARATOR_A.getBytes());
                    fos.write(str_val.getBytes());
                    fos.write(Data.VAL_SEPARATOR_A.getBytes());
                    fos.write(int_val.getBytes());
                    fos.write(Data.VAL_SEPARATOR_B.getBytes());
                }
            } catch (IOException e) {
            } finally {
                try {
                    fos.close();
                } catch (IOException e) { }
            }
        }
        catch (FileNotFoundException e) {}
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
}