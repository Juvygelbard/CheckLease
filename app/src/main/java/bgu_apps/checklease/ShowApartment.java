package bgu_apps.checklease;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;

import Fragments.ApartmentListFragment;

import java.io.IOException;
import java.util.ArrayList;
import data.Apartment;
import data.Data;
import data.Field;
import data.Value;
import db_handle.PicturesDB;

public class ShowApartment extends AppCompatActivity {
    private Apartment _apartment;
    private int _appIndex;
    private LinearLayout _imageGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_apartment);

        _appIndex = this.getIntent().getIntExtra("AppIndex", 0);
        _apartment = ApartmentListFragment._apartments.get(_appIndex);
        _imageGallery = (LinearLayout)this.findViewById(R.id.show_image_gallery);

        TextView address = (TextView)this.findViewById(R.id.txvShowAddress);
        address.setText(_apartment.getAddress());
        TextView price = (TextView)this.findViewById(R.id.txvShowPrice);
        price.setText("" + _apartment.getGivenPrice());
        TextView calcPrice = (TextView)this.findViewById(R.id.txvShowCalcPrice);
        calcPrice.setText("(מחיר מומלץ לדירה זו: " + _apartment.getCalcPrice() + ")");

        ImageView rateView = (ImageView)this.findViewById(R.id.show_rate_house);
        int rate = Data.getRate(_apartment.getCalcPrice(), _apartment.getGivenPrice());
        switch(rate){
            case 0:
                rateView.setImageResource(R.drawable.home_grey);
                break;
            case 1:
                rateView.setImageResource(R.drawable.home_green);
                break;
            case 2:
                rateView.setImageResource(R.drawable.home_orange);
                break;
            case 3:
                rateView.setImageResource(R.drawable.home_red);
                break;
        }

        ArrayList<Field> fields = Data.getAllFields();
        ArrayList<Value> values = Field.matchParmasToFields(_apartment);

        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.mainShowLayout);

        for(int i=0; i<fields.size(); i++){
            final Field field = fields.get(i);
            final Value value = values.get(i);
            LinearLayout fieldLayout = null;

            switch (field.getType()){
                case Field.NUMBER:
                    fieldLayout = (LinearLayout)inflater.inflate(R.layout.show_number_template, null);
                    TextView numTitle = (TextView)fieldLayout.findViewById(R.id.txvNumberTitle);
                    TextView numText = (TextView)fieldLayout.findViewById(R.id.txvShowNumber);
                    numTitle.setText(field.getName());
                    numText.setText("" + value.getIntValue());
                    break;
                case Field.TEXT:
                    fieldLayout = (LinearLayout)inflater.inflate(R.layout.show_text_template, null);
                    TextView textTitle = (TextView)fieldLayout.findViewById(R.id.txvTextTitle);
                    TextView textText = (TextView)fieldLayout.findViewById(R.id.txvShowText);
                    textTitle.setText(field.getName());
                    textText.setText(value.getStrValue());
                    break;
                case Field.PHONE:
                    fieldLayout = (LinearLayout)inflater.inflate(R.layout.show_phone_template, null);
                    TextView phoneTitle = (TextView)fieldLayout.findViewById(R.id.txvPhoneTitle);
                    TextView phoneText = (TextView)fieldLayout.findViewById(R.id.txvShowPhone);
                    ImageButton callButton = (ImageButton)fieldLayout.findViewById(R.id.callPhone);

                    phoneTitle.setText(field.getName());
                    phoneText.setText("" + value.getStrValue());
                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent dial = new Intent(Intent.ACTION_DIAL);
                            dial.setData(Uri.parse("tel:" + value.getStrValue()));
                            ShowApartment.this.startActivity(dial);
                        }
                    });
                    break;
                case Field.MULTISELECT:
                    fieldLayout = (LinearLayout)inflater.inflate(R.layout.show_spinner_template, null);
                    TextView spinnerTitle = (TextView)fieldLayout.findViewById(R.id.txvSpinnerTitle);
                    TextView spinnerText = (TextView)fieldLayout.findViewById(R.id.txvShowSpinner);
                    spinnerTitle.setText(field.getName());
                    String[] choises = field.getEx1().split(";");
                    String picked = choises[value.getIntValue()];
                    spinnerText.setText(picked);
                    break;
                case Field.CHECKBOX:
                    fieldLayout = (LinearLayout)inflater.inflate(R.layout.show_checkbox_template, null);
                    TextView checkboxTitle = (TextView)fieldLayout.findViewById(R.id.txvCheckboxTitle);
                    ImageView checkedImg = (ImageView)fieldLayout.findViewById(R.id.viewChecked);
                    checkboxTitle.setText(field.getName());
                    if(value.getIntValue() == 1)
                        checkedImg.setImageResource(R.drawable.checkbox_yes);
                    else
                        checkedImg.setImageResource(R.drawable.checkbox_no);
                    break;
            }
            mainLayout.addView(fieldLayout, mainLayout.getChildCount()-4);
        }

        PicturesDB picDB = PicturesDB.getInstance();
        ArrayList<String> picPaths = picDB.getPicturePathList(_apartment.getId());
        for(String path: picPaths){
            try {
                Uri img = Uri.parse(path);
                addImageToDisplay(img);
            } catch (IOException e) {
                // problem with this image
                picDB.removePic(path);
            }
        }
    }

    public void editNow(View view){
        Intent editApp = new Intent(this, EditApartment.class);
        editApp.putExtra("AppIndex", _appIndex);
        startActivity(editApp);
        finish();
    }

    private Bitmap scaleImage(Bitmap org){
        int newHeight = 200;
        int newWidth = (int)(((float)newHeight / (float)org.getHeight()) * (float)org.getWidth());
        Bitmap scaledImage = Bitmap.createScaledBitmap(org, newWidth, newHeight, false);
        return scaledImage;
    }

    private void addImageToDisplay(final Uri linkedFile) throws IOException {
        // get bitmap and scale it
        final Bitmap image = MediaStore.Images.Media.getBitmap(ShowApartment.this.getContentResolver(), linkedFile);
        Bitmap scaledImage = scaleImage(image);
        // add imageview and logic
        final ImageView img = new ImageView(this.getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        img.setLayoutParams(params);
        img.setPadding(5, 5, 5, 5);
        img.setImageBitmap(scaledImage);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openImage = new Intent();
                openImage.setAction(Intent.ACTION_VIEW);
                openImage.setDataAndType(linkedFile, "image/*");
                ShowApartment.this.startActivity(openImage);
            }
        });
        _imageGallery.addView(img);
    }
}
