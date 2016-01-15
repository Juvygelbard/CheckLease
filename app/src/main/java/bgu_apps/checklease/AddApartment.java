package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.util.ArrayList;
import android.view.ViewGroup.LayoutParams;
import data.Data;
import data.Field;

import static bgu_apps.checklease.R.layout.activity_add_apartment;

public class AddApartment extends AppCompatActivity {
    ArrayList<Field> _fields;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_apartment);
        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainLayout);
        _fields = Data.getAllFields();
        // create layout params, to be used later on.
        LayoutParams wrap_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutParams match_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        for(Field field: _fields) {
            // create new layout
            LinearLayout layout = new LinearLayout(this.getApplicationContext());
            layout.setLayoutParams(wrap_params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            // create textview to display field name
            TextView name = new TextView(this.getApplicationContext());
            name.setLayoutParams(wrap_params);
            name.setText(field.getName());
            // add field itself
            switch (field.getType()) {
                case Field.CHECKBOX:
                    CheckBox checkbox = new CheckBox(this.getApplicationContext());
                    checkbox.setLayoutParams(wrap_params);
                    layout.addView(checkbox);
                    break;
                case Field.NUMBER:
                    EditText edittext = new EditText(this.getApplicationContext());
                    edittext.setLayoutParams(wrap_params);
                    edittext.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    layout.addView(edittext);
                    break;
                case Field.MULTISELECT:
                    Spinner spinner = new Spinner(this.getApplicationContext());
                    spinner.setLayoutParams(wrap_params);
                    String[] items = field.getEx2().split(";");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, items);
                    spinner.setAdapter(adapter);
                    layout.addView(spinner);
                    break;
            }
            mainLayout.addView(layout);
        }

    }
}
