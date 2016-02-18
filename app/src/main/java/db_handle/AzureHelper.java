package db_handle;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import android.content.Context;
import java.util.ArrayList;
import data.Data;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import data.Field;


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

    public void addFields(ArrayList<Field> fields){
        MobileServiceTable<fieldReceiver> table = _db.getTable("fields", fieldReceiver.class);
        for(Field field: fields){
            fieldReceiver toAdd = new fieldReceiver();
            toAdd._city = Data.getCity();
            toAdd._name = field.getName();
            toAdd._type = field.getType();
            toAdd._f_id = field.getId();
            toAdd._ex1 = field.getEx1();
            toAdd._ex2 = field.getEx2();
            toAdd._formula = field.getFormula();
            table.insert(toAdd);
        }
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