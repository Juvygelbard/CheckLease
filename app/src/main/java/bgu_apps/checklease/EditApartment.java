package bgu_apps.checklease;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import android.net.Uri;

import android.widget.Toast;
import Fragments.ApartmentListFragment;
import data.Apartment;
import data.Value;
import db_handle.ApartmentDB;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.os.Environment;
import android.widget.EditText;
import android.view.Gravity;
import java.util.ArrayList;
import data.Data;
import java.io.File;
import android.graphics.Color;
import data.Field;
import db_handle.PicturesDB;
import java.util.HashSet;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;

// for google places api
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import static bgu_apps.checklease.R.layout.activity_edit_apartment;

public class EditApartment extends AppCompatActivity {
    private int _appIndex;
    private boolean _newApartment;
    private boolean _imageEditMode = false;
    private HashSet<String> _markedUri = new HashSet<String>();
    private HashSet<ImageView> _markedPics = new HashSet<ImageView>();

    private ArrayList<Field> _fieldsRaw;
    private ArrayList<View> _fieldsWidgets;
    private Apartment _currApartment;
    private EditText _txtGivenPrice;
    private LinearLayout _addPhotoPlatte;
    private LinearLayout _removePhotoPlatte;
    private LinearLayout _imageGallery;
    private ImageButton _rateHouse;

    private static Uri _currImageUri;
    private PicturesDB _picsDB;

    private Place _address;
    private int _calcPrice;
    private int _givenPrice;
    private int _rate;

    private int CAMERA = 0;
    private int GALLERY = 1;
    private final int BORDER = Color.parseColor("#98D086");
    private final int NO_BORDER = Color.parseColor("#d7d7d7");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_edit_apartment);

        _appIndex = this.getIntent().getIntExtra("AppIndex", -1);
        _newApartment = _appIndex == -1;
        _picsDB = PicturesDB.getInstance();

        final LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainLayout);
        _imageGallery = (LinearLayout)mainLayout.findViewById(R.id.image_gallery);
        _addPhotoPlatte = (LinearLayout)mainLayout.findViewById(R.id.add_photo_platte);
        _removePhotoPlatte = (LinearLayout)mainLayout.findViewById(R.id.remove_photo_platte);
        _rateHouse = (ImageButton)this.findViewById(R.id.rate_house);
        _fieldsRaw = Data.getAllFields();
        _fieldsWidgets = new ArrayList<View>();
        LayoutInflater inflater = this.getLayoutInflater();

        // set full address filter
        PlaceAutocompleteFragment _addressView = (PlaceAutocompleteFragment)this.getFragmentManager().findFragmentById(R.id.acfAddress);
        AutocompleteFilter fullAddress = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
        _addressView.setFilter(fullAddress);

        _addressView.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                _address = place;
            }

            @Override
            public void onError(Status status) {

            }
        });

        // build page
        TextView title = (TextView)this.findViewById(R.id.txvTitle);
        _txtGivenPrice = (EditText)this.findViewById(R.id.txtPrice);
        if(!_newApartment){
            title.setText("עריכת דירה");
            _currApartment = ApartmentListFragment._apartments.get(_appIndex);
            _addressView.setText(_currApartment.getValue(Data.ADDRESS_STR).getStrValue());
            EditText apNum = (EditText)this.findViewById(R.id.txtApartmentNum);
            if(_currApartment.getValue(Data.APARTMENT_NUM).getIntValue() != -1)
                apNum.setText("" + _currApartment.getValue(Data.APARTMENT_NUM).getIntValue());
            _txtGivenPrice.setText("" + _currApartment.getValue(Data.GIVEN_PRICE).getIntValue());
        }
        else
            title.setText("דירה חדשה");

        ArrayList<Value> params = Field.matchParmasToFields(_currApartment, _fieldsRaw);
        for(int i=0; i<_fieldsRaw.size(); i++) {
            // check which field we're dealing with
            Field field = _fieldsRaw.get(i);
            Value param = params.get(i);
            switch (field.getType()) {
                case Field.CHECKBOX:
                    LinearLayout checkboxLayout = (LinearLayout)inflater.inflate(R.layout.edit_checkbox_template, null);
                    final CheckBox checkbox = (CheckBox)checkboxLayout.findViewById(R.id.chbCheckbox);
                    TextView checkboxLabel = (TextView)checkboxLayout.findViewById(R.id.txvCheckbox);
                    checkboxLabel.setText(field.getName());
                    checkboxLabel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkbox.callOnClick();
                        }
                    });
                    checkbox.setChecked(param.getIntValue() == 1);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            recalcPrice();
                        }
                    });
                    _fieldsWidgets.add(checkbox);
                    mainLayout.addView(checkboxLayout, mainLayout.getChildCount()-3);
                    break;
                case Field.NUMBER:
                    LinearLayout numberLayout = (LinearLayout)inflater.inflate(R.layout.edit_number_template, null);
                    final EditText editnum = (EditText)numberLayout.findViewById(R.id.txtNumber);
                    TextView numberLabel = (TextView)numberLayout.findViewById(R.id.txvNumber);
                    numberLabel.setText(field.getName());
                    editnum.setText("" + param.getIntValue());

                    editnum.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (editnum.getText().toString().equals(""))
                                editnum.setText("0");
                            recalcPrice();
                        }
                    });
                    _fieldsWidgets.add(editnum);
                    mainLayout.addView(numberLayout, mainLayout.getChildCount()-3);
                    break;
                case Field.MULTISELECT:
                    LinearLayout spinnerLayout = (LinearLayout)inflater.inflate(R.layout.edit_spinner_template, null);
                    Spinner spinner = (Spinner)spinnerLayout.findViewById(R.id.sprSpinner);
                    TextView spinnerLabel = (TextView)spinnerLayout.findViewById(R.id.txvSpinner);
                    spinnerLabel.setText(field.getName());
                    // set spinner objects
                    String[] items = field.getEx1().split(";");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(param.getIntValue());

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            recalcPrice();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { }
                    });
                    _fieldsWidgets.add(spinner);
                    mainLayout.addView(spinnerLayout, mainLayout.getChildCount()-3);
                    break;
                case Field.PHONE:
                    LinearLayout phoneLayout = (LinearLayout)inflater.inflate(R.layout.edit_phone_template, null);
                    final EditText editphone = (EditText)phoneLayout.findViewById(R.id.txtPhone);
                    ImageButton callbutton = (ImageButton)phoneLayout.findViewById(R.id.callButton);
                    final TextView phoneLabel = (TextView)phoneLayout.findViewById(R.id.txvPhone);
                    phoneLabel.setText(field.getName());
                    editphone.setText(param.getStrValue());

                    callbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent dial = new Intent(Intent.ACTION_DIAL);
                            dial.setData(Uri.parse("tel:" + editphone.getText().toString()));
                            EditApartment.this.startActivity(dial);
                        }
                    });
                    _fieldsWidgets.add(editphone);
                    mainLayout.addView(phoneLayout, mainLayout.getChildCount()-3);
                    break;
                case Field.TEXT:
                    LinearLayout textLayout = (LinearLayout)inflater.inflate(R.layout.edit_text_template, null);
                    final EditText edittext = (EditText)textLayout.findViewById(R.id.txtText);
                    TextView textLabel = (TextView)textLayout.findViewById(R.id.txvText);
                    textLabel.setText(field.getName());
                    edittext.setText(param.getStrValue());
                    edittext.setLines(field.getEx2());
                    _fieldsWidgets.add(edittext);
                    mainLayout.addView(textLayout, mainLayout.getChildCount()-3);
                    break;
            }
        }
        // add price field
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

        recalcPrice();

        // create image display
        if(!_newApartment){
            ArrayList<String> picPathList = _picsDB.getPicturePathList(_currApartment.getId());
            for(String path: picPathList){
                try {
                    Uri imageUri = Uri.parse(path);
                    addImageToDisplay(imageUri);
                } catch (IOException e) { // image dosn't exist, remove it from db.
                    _picsDB.removePic(path);
                }
            }
        }

        ArrayList<String> picPathList = _picsDB.getTempPics();
        for(String path: picPathList){
            try {
                Uri imageUri = Uri.parse(path);
                addImageToDisplay(imageUri);
            } catch (IOException e) { // image dosn't exist, remove it from db.
                _picsDB.removePic(path);
            }
        }

        ImageButton fromCamera = (ImageButton)mainLayout.findViewById(R.id.add_photo_camera);
        ImageButton fromGallery = (ImageButton)mainLayout.findViewById(R.id.add_photo_gallery);
        ImageButton removePhoto = (ImageButton)mainLayout.findViewById(R.id.remove_photo);
        ImageButton backToAdd = (ImageButton)mainLayout.findViewById(R.id.back_to_add);
        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File image = getImageFile();
                    _currImageUri = Uri.fromFile(image);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, _currImageUri);
                    EditApartment.this.startActivityForResult(camera, CAMERA);
                } catch (IOException e) {}
            }
        });
        fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                EditApartment.this.startActivityForResult(Intent.createChooser(gallery, "בחירת תמונה"), GALLERY);
            }
        });
        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // build an 'are you sure?' dialog
                AlertDialog.Builder askBuilder = new AlertDialog.Builder(EditApartment.this, R.style.MyMaterialTheme);
                askBuilder.setTitle("האם למחוק את התמונות המסומנות?");
                askBuilder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // delete all selected pics
                        for (String picUri : _markedUri)
                            _picsDB.removePic(picUri);
                        _markedUri.clear();
                        for (ImageView picView : _markedPics)
                            _imageGallery.removeView(picView);
                        _markedPics.clear();
                        if (_imageGallery.getChildCount() == 0)
                            stopPicEdit();
                        dialog.dismiss();
                    }
                });
                askBuilder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                askBuilder.show();
            }
        });
        backToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPicEdit();
            }
        });

    }

    public Apartment extractApartment(int id){
        EditText apartmentNumView = (EditText)this.findViewById(R.id.txtApartmentNum);
        String apartmentNumStr = apartmentNumView.getText().toString();
        int apartmentNum = -1;
        if(!apartmentNumStr.equals(""))
            apartmentNum = Integer.parseInt(apartmentNumStr);
        String addressStr;
        String addressID;
        String addressLat;
        String addressLan;
        if(_address == null) // should only accur if it's an existing apartment!
        {
            addressStr = _currApartment.getValue(Data.ADDRESS_STR).getStrValue();
            addressID = _currApartment.getValue(Data.ADDRESS_ID).getStrValue();
            addressLat = _currApartment.getValue(Data.ADDRESS_LAT).getStrValue();
            addressLan = _currApartment.getValue(Data.ADDRESS_LAN).getStrValue();
        }
        else{ // should always accur for a new apartment
            addressStr = _address.getAddress().toString().replace(", ישראל", "");
            addressID = _address.getId();
            addressLat = "" + _address.getLatLng().latitude;
            addressLan = "" + _address.getLatLng().longitude;
        }
        Apartment toAdd = new Apartment(id);
        ArrayList<Value> vals = extractVals();
        for(int i=0; i<vals.size(); i++) // add values
            toAdd.addValue(_fieldsRaw.get(i).getId(), vals.get(i));
        toAdd.addValue(Data.ADDRESS_ID, addressID);
        toAdd.addValue(Data.ADDRESS_STR, addressStr);
        toAdd.addValue(Data.ADDRESS_LAT, addressLat);
        toAdd.addValue(Data.ADDRESS_LAN, addressLan);
        toAdd.addValue(Data.APARTMENT_NUM, apartmentNum);
        toAdd.addValue(Data.GIVEN_PRICE, _givenPrice);
        toAdd.addValue(Data.CALC_PRICE, _calcPrice);
        return toAdd;
    }

    public void saveAndFinish(View view){
        if(_newApartment && _address == null){ // no address was picked!
            Toast msg = Toast.makeText(this.getApplicationContext(), "לא נבחרה כתובת!", Toast.LENGTH_SHORT);
            msg.show();
            return;
        }
        ApartmentDB apartmentDB = ApartmentDB.getInstance(); // get db
        if(_newApartment) {
            int id = Data.getCurrApartmentCounter(); // get new apartment id
            Apartment toAdd = extractApartment(id); // get apartment
            toAdd.addValue(Data.FAVORITE, 0); // set Favorite to non
            _picsDB.saveTempPic(id); // save pics
            apartmentDB.addApartment(toAdd); // save apartment
            Data.increaseApartmentCounter();
        }
        else {
            int id = _currApartment.getId(); // ged edited apartment id
            Apartment toAdd = extractApartment(id); // get apartment
            toAdd.addValue(Data.FAVORITE, _currApartment.getValue(Data.FAVORITE).getIntValue()); // set Favorite to old
            _picsDB.saveTempPic(id); // save newly added pics
            apartmentDB.deleteApartment(id); // remove old apartment details.
            apartmentDB.addApartment(toAdd); // save new apartment.
        }
        ApartmentListFragment.get_instance().refreshList();
        this.finish();
    }

    protected void onDestroy(){
        super.onDestroy();
        _picsDB.removeTempPics();
    }

    private void startPicEdit(){
        _imageEditMode = true;
        // change platte
        _addPhotoPlatte.setVisibility(View.GONE);
        _removePhotoPlatte.setVisibility(View.VISIBLE);
    }

    private void stopPicEdit(){
        _imageEditMode = false;
        // unmark all marked images
        for(ImageView img: _markedPics){
            img.setBackgroundColor(NO_BORDER);
            img.setSelected(false);
        }
        _markedPics.clear();
        _markedUri.clear();
        // change platte
        _removePhotoPlatte.setVisibility(View.GONE);
        _addPhotoPlatte.setVisibility(View.VISIBLE);
    }

    private Bitmap scaleImage(Bitmap org){
        int newHeight = 200;
        int newWidth = (int)(((float)newHeight / (float)org.getHeight()) * (float)org.getWidth());
        Bitmap scaledImage = Bitmap.createScaledBitmap(org, newWidth, newHeight, false);
        return scaledImage;
    }

    private void addImageToDisplay(final Uri linkedFile) throws IOException {
        // get bitmap and scale it
        final Bitmap image = MediaStore.Images.Media.getBitmap(EditApartment.this.getContentResolver(), linkedFile);
        Bitmap scaledImage = scaleImage(image);
        // add imageview and logic
        final ImageView img = new ImageView(this.getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        img.setLayoutParams(params);
        img.setPadding(5, 5, 5, 5);
        img.setImageBitmap(scaledImage);
        img.setOnClickListener(new View.OnClickListener() {
            private void unmark() {
                img.setSelected(false);
                img.setBackgroundColor(NO_BORDER);
                _markedPics.remove(img);
                _markedUri.remove(linkedFile.toString());
            }

            private void mark() {
                img.setSelected(true);
                img.setBackgroundColor(BORDER);
                _markedPics.add(img);
                _markedUri.add(linkedFile.toString());
            }

            @Override
            public void onClick(View v) {
                if (_imageEditMode) {
                    if (img.isSelected())
                        unmark();
                    else
                        mark();
                } else {
                    Intent openImage = new Intent();
                    openImage.setAction(Intent.ACTION_VIEW);
                    openImage.setDataAndType(linkedFile, "image/*");
                    EditApartment.this.startActivity(openImage);
                }
            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!_imageEditMode) {
                    img.setSelected(true);
                    img.setBackgroundColor(BORDER);
                    _markedPics.add(img);
                    _markedUri.add(linkedFile.toString());
                    startPicEdit();
                }
                return true;
            }
        });
        _imageGallery.addView(img);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // adding images when returning from camera/gallery intents.
        if(requestCode == CAMERA && resultCode == RESULT_OK){ // from camera
            try {
                addImageToDisplay(_currImageUri);
                _picsDB.addTempPic(_currImageUri.toString());
            } catch (IOException e) {}
        } else if(requestCode == GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null){ // from gallery
            try {
                Uri imageUri = data.getData();;
                addImageToDisplay(imageUri);
                _picsDB.addTempPic(imageUri.toString());
            } catch (IOException e) {/* file not found */}
        }
    }

    private File getImageFile() throws IOException {
        String fileName = "checklease_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", path);
        return image;
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
                _rateHouse.setImageResource(R.drawable.home_grey);
                break;
            case 1:
                _rateHouse.setImageResource(R.drawable.home_green);
                break;
            case 2:
                _rateHouse.setImageResource(R.drawable.home_orange);
                break;
            case 3:
                _rateHouse.setImageResource(R.drawable.home_red);
                break;
        }
    }

    private ArrayList<Value> extractVals(){
        ArrayList<Value> ans = new ArrayList<Value>();
        for(int i=0; i<_fieldsRaw.size(); i++){
            View currView = _fieldsWidgets.get(i);
            switch(_fieldsRaw.get(i).getType()){
                case Field.NUMBER:
                    int num = Integer.parseInt(((EditText) currView).getText().toString());
                    ans.add(new Value(num));
                    break;
                case Field.CHECKBOX:
                    boolean check = ((CheckBox)currView).isChecked();
                    int newCheck = 0;
                    if(check)
                        newCheck = 1;
                    ans.add(new Value(newCheck));
                    break;
                case Field.MULTISELECT:
                    int selection = ((Spinner)currView).getSelectedItemPosition();
                    ans.add(new Value(selection));
                    break;
                case Field.PHONE:
                case Field.TEXT:
                    String userInput = ((EditText)currView).getText().toString();
                    ans.add(new Value(userInput));
                    break;
            }
        }
        return ans;
    }
}