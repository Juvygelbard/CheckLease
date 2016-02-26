package Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

import adapters.SettingsAdapter;
import bgu_apps.checklease.MainActivity;
import bgu_apps.checklease.R;
import data.Apartment;
import data.Data;
import db_handle.ApartmentDB;

/**
 * Created by user on 09/01/2016.
 */
public class SettingsFragment extends Fragment {
    private ListView _lv;
    private static SettingsAdapter _adapter;

    public SettingsFragment(){}

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View layout = inflater.inflate(R.layout.fragment_settings, container, false);

        _lv = (ListView) layout.findViewById(R.id.SettingsList);

        _adapter = new SettingsAdapter(inflater);
        _lv.setAdapter(_adapter);

        _lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        String[] cityNames = new String[Data.getAllCities().size()];
                        for (int i = 0; i < cityNames.length; i++)
                            cityNames[i] = Data.getAllCities().get(i).get_name();
                        ListDialogFragment dialogA = new ListDialogFragment("בחר עיר", cityNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Data.setCity(Data.getAllCities().get(which));
                                //TODO: sherd PREFRENCES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                        });
                        dialogA.show(getFragmentManager(), "בחירת עיר");
                        break;
                    }
                    case 1: {
                        String[] sortTypes = {"הדירות הכי כדאיות", "הדירות המועדפות שלי", "תאריך הוספת הדירות", "מהמחיר הנמוך לגבוה", "מהמחיר הגבוה לנמוך"};
                        ListDialogFragment dialogB = new ListDialogFragment("מיין את הדירות לפי", sortTypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Data.setSortBy(which);
                            }
                        });
                        dialogB.show(getFragmentManager(), "מיון הדירות");
                        break;
                    }
                    case 2: {
                        YesNoDialogFragment dialogC = new YesNoDialogFragment("האם ברצונך לשתף עם העמותה את נתוני הדירות שלך לצרכי סטטיסטיקה בלבד?"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Data.setIsDataShared(true);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Data.setIsDataShared(false);
                            }
                        });
                        dialogC.show(getFragmentManager(), "שיתוף");
                        break;
                    }
                    case 3: {
                        if (!(Data.getDeletedApartments().isEmpty())) {
                            String[] apartments = new String[Data.getDeletedApartments().size()];
                            for (int i = 0; i < apartments.length; i++)
                                apartments[i] = Data.getDeletedApartments().get(i).getAddress();

                            ListDialogFragment dialogD = new ListDialogFragment("בחר את הדירה שתרצה לשחזר", apartments, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ApartmentDB.getInstance().addApartment(Data.getDeletedApartments().get(which));
                                    MainActivity._fullListFragment.refreshList();
                                    MainActivity._favListFragment.refreshList();
                                    Context context = getContext();
                                    CharSequence text = "הדירה שוחזרה";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            });
                            dialogD.show(getFragmentManager(), "שחזור דירות");
                        } else {
                            Context context = getContext();
                            CharSequence text = "אין דירות שנמחקו לאחרונה";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        break;
                    }
                    case 4: {
                        if (!(ApartmentListFragment._apartments.isEmpty())) {
                            YesNoDialogFragment dialogE = new YesNoDialogFragment("האם בטוח שברצונך למחוק את כל רשימת הדירות?",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Data.getDeletedApartments().addAll(ApartmentListFragment._apartments);
                                            ApartmentDB.getInstance().deleteAll();
                                            MainActivity._fullListFragment.refreshList();
                                            MainActivity._favListFragment.refreshList();
                                            Toast toast = Toast.makeText(getContext(), "כל הדירות שברשימה נמחקו", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Context context = getContext();
                                    CharSequence text = "רשימת הדירות נותרה ללא שינוי";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            });
                        } else {
                            Context context = getContext();
                            CharSequence text = "אין דירות ברשימה";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        break;
                    }

                }
            }
        });


        return layout;
    }


}
