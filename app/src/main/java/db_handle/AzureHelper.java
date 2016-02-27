package db_handle;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import android.content.Context;
import java.util.ArrayList;

import data.Apartment;
import data.City;
import data.Data;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import data.Field;
import data.Value;

/**
 * Created by Juvy on 05/02/2016.
 */
public class AzureHelper {
    private static AzureHelper _instance;
    private MobileServiceClient _db;

    private AzureHelper(){}

    public static void init(Context context){
        _instance = new AzureHelper();
        try {
            _instance._db = new MobileServiceClient("https://checklease.azurewebsites.net", context);
        } catch (MalformedURLException e) {}
    }

    public static AzureHelper getInstance(){
        return _instance;
    }

    public ArrayList<Field> getFieldList(String city) throws MobileServiceException, ExecutionException, InterruptedException {
        MobileServiceTable<fieldReceiver> table = _db.getTable("fields", fieldReceiver.class);
        ArrayList<Field> ans = new ArrayList<Field>();
        ArrayList<fieldReceiver> raw = null;
        raw = table.where().field("city").eq(city).execute().get();
        for(fieldReceiver curr: raw)
            ans.add(curr.generateField());

        return ans;
    }

    public ArrayList<City> getCityList() throws MobileServiceException, ExecutionException, InterruptedException {
        MobileServiceTable<cityReceiver> table = _db.getTable("cities", cityReceiver.class);
        ArrayList<City> ans = new ArrayList<City>();
        ArrayList<cityReceiver> raw = null;
        raw = table.execute().get();
        for(cityReceiver curr: raw)
            ans.add(curr.extractCity());

        return ans;
    }

    public void addCity(City city){
        MobileServiceTable<cityReceiver> table = _db.getTable("cities", cityReceiver.class);
        cityReceiver toAdd = new cityReceiver();
        toAdd._city_id = city.get_id();
        toAdd._city_name = city.get_name();
        toAdd._lat = city.getLatLan().latitude;
        toAdd._lan = city.getLatLan().longitude;
        toAdd._zoom = city.getZoom();
        table.insert(toAdd);
    }

    public void addFields(ArrayList<Field> fields){
        MobileServiceTable<fieldReceiver> table = _db.getTable("fields", fieldReceiver.class);
        for(Field field: fields){
            fieldReceiver toAdd = new fieldReceiver();
            toAdd._city = Data.getCityID();
            toAdd._name = field.getName();
            toAdd._type = field.getType();
            toAdd._f_id = field.getId();
            toAdd._ex1 = field.getEx1();
            toAdd._ex2 = field.getEx2();
            toAdd._formula = field.getFormula();
            table.insert(toAdd);
        }
    }

    public void addApartment(Apartment toAdd){
        MobileServiceTable<apartmentSender> table = _db.getTable("apartments", apartmentSender.class);
        String app_loc_id = toAdd.getValue(Data.ADDRESS_ID).getStrValue();
        int app_num_id = toAdd.getValue(Data.APARTMENT_NUM).getIntValue();
        String city = Data.getCityID();

        Iterator<Map.Entry<Integer, Value>> apartmentFeature = toAdd.getFeatureIterator();
        while(apartmentFeature.hasNext()){
            Map.Entry<Integer, Value> currEntry = apartmentFeature.next();
            apartmentSender feature = new apartmentSender();
            feature._loc_id = app_loc_id;
            feature._app_num = app_num_id;
            feature._city = city;
            feature._field_id = currEntry.getKey();
            feature._int_val = currEntry.getValue().getIntValue();
            feature._str_val = currEntry.getValue().getStrValue();
            table.insert(feature);
        }
    }
    class cityReceiver{
        @com.google.gson.annotations.SerializedName("ID")
        private String _col_id;

        @com.google.gson.annotations.SerializedName("city_id")
        private String _city_id;

        @com.google.gson.annotations.SerializedName("city_str")
        private String _city_name;

        @com.google.gson.annotations.SerializedName("city_lat")
        private double _lat;

        @com.google.gson.annotations.SerializedName("city_lan")
        private double _lan;

        @com.google.gson.annotations.SerializedName("city_zoom")
        private float _zoom;

        public City extractCity(){
            return new City(_city_name, _city_id, _lat, _lan, _zoom);
        }
    }

    class apartmentSender{
        @com.google.gson.annotations.SerializedName("ID")
        private String _col_id;

        @com.google.gson.annotations.SerializedName("apartment_loc_id")
        private String _loc_id;

        @com.google.gson.annotations.SerializedName("apartment_num_id")
        private int _app_num;

        @com.google.gson.annotations.SerializedName("city")
        private String _city;

        @com.google.gson.annotations.SerializedName("field_id")
        private int _field_id;

        @com.google.gson.annotations.SerializedName("int_val")
        private int _int_val;

        @com.google.gson.annotations.SerializedName("str_val")
        private String _str_val;
    }

    class fieldReceiver{
        @com.google.gson.annotations.SerializedName("ID")
        private String _col_id;

        @com.google.gson.annotations.SerializedName("f_id")
        private int _f_id;

        @com.google.gson.annotations.SerializedName("name")
        private String _name;

        @com.google.gson.annotations.SerializedName("type")
        private int _type;

        @com.google.gson.annotations.SerializedName("ex1")
        private String _ex1;

        @com.google.gson.annotations.SerializedName("ex2")
        private int _ex2;

        @com.google.gson.annotations.SerializedName("formula")
        private String _formula;

        @com.google.gson.annotations.SerializedName("city")
        private String _city;

        @com.google.gson.annotations.SerializedName("order_i")
        private int _order;

        public Field generateField(){
            Field ans = new Field(_f_id, _name, _type, _ex1, _ex2, _formula);
            ans.setOrder(_order);
            return ans;
        }
    }

    // TODO: DELETE THIS!
    public void dummy(){
        ArrayList<Field> fields = new ArrayList<Field>();

        Field neighborhood = new Field(3, "שכונה", Field.MULTISELECT, "א';ב';ג';ד';ו'", 4, "X+100Y");
        neighborhood.setOrder(1);
        fields.add(neighborhood);

        Field appartmentSize = new Field(0, "גודל דירה", Field.NUMBER, "", 100, "(1+Y/100)*X");
        appartmentSize.setOrder(2);
        fields.add(appartmentSize);

        Field washingMachine = new Field(1, "מכונת כביסה", Field.CHECKBOX, "", 0, "X+200Y");
        washingMachine.setOrder(3);
        fields.add(washingMachine);

        Field phoneRes = new Field(2, "טלפון דיירים", Field.PHONE, "", 0, "X");
        phoneRes.setOrder(4);
        fields.add(phoneRes);

        Field desc = new Field(4, "תיאור כללי", Field.TEXT, "תאר את הדירה.", 4, "X");
        desc.setOrder(5);
        fields.add(desc);

        addFields(fields);
    }
}