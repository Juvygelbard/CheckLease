package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.CompoundButton;
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
    TextView _calcPriceView;
    int _calcPrice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_apartment);
        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainLayout);
        _fieldsRaw = Data.getAllFields();
        _fieldsWidgets = new ArrayList<View>();
        // create layout params, to be used later on.
        LayoutParams field_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
        LayoutParams text_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(Field field: _fieldsRaw) {
            // create new layout
            LinearLayout layout = new LinearLayout(this.getApplicationContext());
            layout.setLayoutParams(layout_params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            // create textview to display field name
            TextView name = new TextView(this.getApplicationContext());
            name.setLayoutParams(text_params);
            name.setGravity(Gravity.RIGHT);
            name.setText(field.getName());
            name.setTextColor(Color.parseColor("#000000")); // TODO: take color from XML
            // add field itself
            switch (field.getType()) {
                case Field.CHECKBOX:
                    CheckBox checkbox = new CheckBox(this.getApplicationContext());
                    checkbox.setLayoutParams(field_params);
                    checkbox.setGravity(Gravity.RIGHT);
                    if(field.getEx2() == 1)
                        checkbox.setChecked(true);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            recalcPrice();
                        }
                    });
                    _fieldsWidgets.add(checkbox);
                    layout.addView(checkbox);
                    break;
                case Field.NUMBER:
                    EditText edittext = new EditText(this.getApplicationContext());
                    edittext.setLayoutParams(field_params);
                    edittext.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    edittext.setGravity(Gravity.RIGHT);
                    edittext.setText("" + field.getEx2());
                    edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            recalcPrice();
                            return true;
                        }
                    });
                    _fieldsWidgets.add(edittext);
                    layout.addView(edittext);
                    break;
                case Field.MULTISELECT:
                    Spinner spinner = new Spinner(this.getApplicationContext());
                    spinner.setLayoutParams(field_params);
                    String[] items = field.getEx1().split(";");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner.setGravity(Gravity.RIGHT);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(field.getEx2());
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            recalcPrice();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    _fieldsWidgets.add(spinner);
                    layout.addView(spinner);
                    break;
            }
            layout.addView(name);
            mainLayout.addView(layout);
        }
        _calcPriceView = new TextView(this.getApplicationContext());
        _calcPriceView.setLayoutParams(text_params);
        _calcPriceView.setTextColor(Color.parseColor("#000000"));
        _calcPriceView.setTextSize(20);
        recalcPrice();
        mainLayout.addView(_calcPriceView);
    }

    private void recalcPrice(){
        _calcPrice = Field.calculateFieldList(extractVals(), _fieldsRaw);
        _calcPriceView.setText("מחיר מומלץ: "+ _calcPrice);
    }

    private ArrayList<Integer> extractVals(){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        for(View field: _fieldsWidgets){
            if(field instanceof EditText){
                String userInput = ((EditText)field).getText().toString();
                if(userInput.equals(""))
                    userInput = "0";
                ans.add(new Integer(userInput));
            } else if(field instanceof CheckBox){
                boolean userInput = ((CheckBox)field).isChecked();
                int newInput = 0;
                if(userInput)
                    newInput = 1;
                ans.add(new Integer(newInput));
            } else if(field instanceof Spinner){
                int userInput = ((Spinner)field).getSelectedItemPosition();
                ans.add(new Integer(userInput));
            }
        }
        return ans;
    }
}