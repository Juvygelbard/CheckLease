package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import data.Data;
import data.Field;

public class AddApartment extends AppCompatActivity {
    ArrayList<Field> _fields;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _fields = Data.getAllFields();
        for(Field field: _fields){
            switch (field.getType()){

            }
        }

        setContentView(R.layout.activity_add_apartment);
    }
}
