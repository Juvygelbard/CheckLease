package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.view.Gravity;
import java.util.ArrayList;
import android.view.ViewGroup.LayoutParams;
import data.Data;
import android.graphics.Color;
import data.Field;
import android.view.View;

import static bgu_apps.checklease.R.layout.activity_add_apartment;

public class AddApartment extends AppCompatActivity {
    ArrayList<Field> _fieldsRaw;
    ArrayList<View> _fieldsWidgets;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_apartment);
        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainLayout);
        _fieldsRaw = Data.getAllFields();
        // create layout params, to be used later on.
        LayoutParams field_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 4f);
        LayoutParams name_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(Field field: _fieldsRaw) {
            // create new layout
            LinearLayout layout = new LinearLayout(this.getApplicationContext());
            layout.setLayoutParams(layout_params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            // create textview to display field name
            TextView name = new TextView(this.getApplicationContext());
            name.setLayoutParams(name_params);
            name.setGravity(Gravity.LEFT);
            name.setText(field.getName());
            name.setTextColor(Color.parseColor("#000000")); // TODO: take color from XML
            // add field itself
            switch (field.getType()) {
                case Field.CHECKBOX:
                    CheckBox checkbox = new CheckBox(this.getApplicationContext());
                    checkbox.setLayoutParams(field_params);
                    checkbox.setGravity(Gravity.RIGHT);
                    if(field.getEx2()==1)
                        checkbox.setChecked(true);
                    // _fieldsWidgets.add(checkbox);
                    layout.addView(checkbox);
                    break;
                case Field.NUMBER:
                    EditText edittext = new EditText(this.getApplicationContext());
                    edittext.setLayoutParams(field_params);
                    edittext.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    edittext.setGravity(Gravity.RIGHT);
                    edittext.setMinWidth(15);
                    edittext.setText(field.getEx1());
                    layout.addView(edittext);
                    break;
                case Field.MULTISELECT:
                    Spinner spinner = new Spinner(this.getApplicationContext());
                    spinner.setLayoutParams(field_params);
                    String[] items = field.getEx1().split(";");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner.setMinimumWidth(15);
                    spinner.setGravity(Gravity.RIGHT);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(field.getEx2());
                    layout.addView(spinner);
                    break;
            }
            layout.addView(name);
            mainLayout.addView(layout);
        }
    }

    private ArrayList<Integer> extractVals(){
        return null;
    }
}
