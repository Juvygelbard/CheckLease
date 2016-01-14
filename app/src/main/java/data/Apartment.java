package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import data.Value;

/**
 * Created by user on 30/12/2015.
 */
public class Apartment {
    private HashMap<Integer, Value> _apartmentDetails;
    private int _id;

    public Apartment(int id){
        _apartmentDetails = new HashMap<Integer , Value>();
        _id = id;
    }

    public Value getValue(int i){
        if (_apartmentDetails.containsKey(i))
            return _apartmentDetails.get(i);
        else
            throw new NullPointerException("Value " + i + " does not exist for this apartment");
    }

    public int getId(){
        return this._id;
    }

    public Iterator<HashMap.Entry<Integer, Value>> getFeatureIterator(){
        Iterator<HashMap.Entry<Integer, Value>> iterator = _apartmentDetails.entrySet().iterator();
        return iterator;
    }

    public void addValue(int id, int num){
        Value value = new Value(num);
        _apartmentDetails.put(id ,value);
    }

    public void addValue(int id, String string){
        Value value = new Value(string);
        _apartmentDetails.put(id, value);
    }

    public String getAddress(){
        String street = this.getValue(Data.STREET).getStrValue();
        int building = this.getValue(Data.BUILDING).getIntValue();
        int numAppartment = this.getValue(Data.NUM_APARTMENT).getIntValue();
        if(numAppartment == -1)
            return street + " " + building;
        else
            return street + " " + building + "/" + numAppartment;
    }

    public boolean isFavorite(){
        return _apartmentDetails.get(Data.FAVORITE).getIntValue() == 1;
    }
}