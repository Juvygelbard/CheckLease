package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ImageButton;
import android.text.TextWatcher;
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
import data.Data;
import android.graphics.Color;
import data.Field;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;

// for google places api
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import static bgu_apps.checklease.R.layout.activity_add_apartment;

public class AddApartment extends AppCompatActivity {
    private boolean _addNew;

    private ArrayList<Field> _fieldsRaw;
    private ArrayList<View> _fieldsWidgets;
    private TextView _calcPriceView;
    private EditText _txtGivenPrice;
    private LinearLayout _imageGallery;

    private ArrayList<Bitmap> _imagesToDisplay;

    private int _calcPrice;
    private int _givenPrice;
    private int _rate;

    private int CAMERA = 0;
    private int GALLERY = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_apartment);

        // TODO: DELETE THIS!
        _addNew = true;

        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainLayout);
        _imageGallery = (LinearLayout)mainLayout.findViewById(R.id.image_gallery);
        _fieldsRaw = Data.getAllFields();
        _fieldsWidgets = new ArrayList<View>();
        if (_imagesToDisplay == null)
            _imagesToDisplay = new ArrayList<Bitmap>();
        LayoutInflater inflater = this.getLayoutInflater();

        // set full address filter
        PlaceAutocompleteFragment address = (PlaceAutocompleteFragment)this.getFragmentManager().findFragmentById(R.id.acfAddress);
        AutocompleteFilter fullAddress = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
        address.setFilter(fullAddress);

        // build page
        for(Field field: _fieldsRaw) {
            // check which field we're dealing with
            switch (field.getType()) {
                case Field.CHECKBOX:
                    LinearLayout checkboxLayout = (LinearLayout)inflater.inflate(R.layout.checkbox_template, null);
                    final CheckBox checkbox = (CheckBox)checkboxLayout.findViewById(R.id.chbCheckbox);
                    TextView checkboxLabel = (TextView)checkboxLayout.findViewById(R.id.txvCheckbox);
                    checkboxLabel.setText(field.getName());
                    checkboxLabel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkbox.callOnClick();
                        }
                    });
                    if(field.getEx2() == 1)
                        checkbox.setChecked(true);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            recalcPrice();
                        }
                    });
                    _fieldsWidgets.add(checkbox);
                    mainLayout.addView(checkboxLayout, mainLayout.getChildCount()-2);
                    break;
                case Field.NUMBER:
                    LinearLayout numberLayout = (LinearLayout)inflater.inflate(R.layout.number_template, null);
                    final EditText edittext = (EditText)numberLayout.findViewById(R.id.txtNumber);
                    TextView numberLabel = (TextView)numberLayout.findViewById(R.id.txvNumber);
                    numberLabel.setText(field.getName());
                    edittext.setText("" + field.getEx2());
                    edittext.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) { }
                        @Override
                        public void afterTextChanged(Editable s) {
                            recalcPrice();
                        }
                    });
                    _fieldsWidgets.add(edittext);
                    mainLayout.addView(numberLayout, mainLayout.getChildCount()-2);
                    break;
                case Field.MULTISELECT:
                    LinearLayout spinnerLayout = (LinearLayout)inflater.inflate(R.layout.spinner_template, null);
                    Spinner spinner = (Spinner)spinnerLayout.findViewById(R.id.sprSpinner);
                    TextView spinnerLabel = (TextView)spinnerLayout.findViewById(R.id.txvSpinner);
                    spinnerLabel.setText(field.getName());
                    // set spinner objects
                    String[] items = field.getEx1().split(";");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(field.getEx2());
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            recalcPrice();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { }
                    });
                    _fieldsWidgets.add(spinner);
                    mainLayout.addView(spinnerLayout, mainLayout.getChildCount()-2);
                    break;
            }
        }
        // add price field
        _txtGivenPrice = (EditText)this.findViewById(R.id.txtPrice);
        _txtGivenPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_txtGivenPrice.getText().equals(""))
                    _txtGivenPrice.setText("0");
                recalcPrice();
            }
        });

        // TODO: delete this, temporary
        LinearLayout.LayoutParams price_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        price_params.gravity = Gravity.RIGHT;
        _calcPriceView = new TextView(this.getApplicationContext());
        _calcPriceView.setLayoutParams(price_params);
        _calcPriceView.setTextColor(Color.parseColor("#000000"));
        _calcPriceView.setTextSize(20);
        mainLayout.addView(_calcPriceView, mainLayout.getChildCount()-1);
        recalcPrice();

        // handle images
        for(Bitmap image: _imagesToDisplay){
            addImageToDisplay(image);
        }

        ImageButton fromCamera = (ImageButton)mainLayout.findViewById(R.id.add_photo_camera);
        ImageButton fromGallery = (ImageButton)mainLayout.findViewById(R.id.add_photo_gallery);
        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                AddApartment.this.startActivityForResult(camera, CAMERA);
            }
        });
    }

    private void addImageToDisplay(Bitmap image){
        ImageView img = new ImageView(this.getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img.setLayoutParams(params);
        img.setImageBitmap(image);
        _imageGallery.addView(img);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // adding images when returning from camera/gallery intents.
        if(requestCode == CAMERA && resultCode==RESULT_OK){
            Bitmap photo = (Bitmap)data.getExtras().get("data");
            _imagesToDisplay.add(photo);
            addImageToDisplay(photo);
        }
    }

    private void recalcPrice(){
        // get all necessary data
        _calcPrice = Field.calculateFieldList(extractVals(), _fieldsRaw);
        String price = _txtGivenPrice.getText().toString();
        if(price.equals(""))
            price = "0";
        _givenPrice = Integer.parseInt(price);
        _rate = Data.getRate(_calcPrice, _givenPrice);

        // determine color
        switch(_rate){
            case 0:
                _txtGivenPrice.setBackgroundColor(Color.parseColor("#81c65e"));
                break;
            case 1:
                _txtGivenPrice.setBackgroundColor(Color.parseColor("#ff9424"));
                break;
            case 2:
                _txtGivenPrice.setBackgroundColor(Color.parseColor("#ff1b1b"));
                break;
        }
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