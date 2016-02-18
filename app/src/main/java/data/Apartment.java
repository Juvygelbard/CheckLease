package data;

import java.util.HashMap;
import java.util.Iterator;

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

    public Apartment(int id, HashMap<Integer , Value> apartmentDetails){
        _apartmentDetails = apartmentDetails;
        _id = id;
    }

    public Value getValue(int i){
        if (_apartmentDetails.containsKey(i))
            return _apartmentDetails.get(i);
        else
            throw new NullPointerException("Value " + i + " does not exist for this apartment");
    }

    public boolean hasField(int id){
        return _apartmentDetails.containsKey(id);
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

    public void addValue(int id, Value val){
        _apartmentDetails.put(id, val);
    }

    public String getAddress(){
        String address = this.getValue(Data.ADDRESS_STR).getStrValue();
        int apartmentNum = this.getValue(Data.APARTMENT_NUM).getIntValue();
        if(apartmentNum == -1)
            return address;
        else
            return address + "/" + apartmentNum;
    }

    public void setFavorite(boolean fav){
        if(fav)
            _apartmentDetails.put(Data.FAVORITE, new Value(1));
        else
            _apartmentDetails.put(Data.FAVORITE, new Value(0));
    }

    public boolean isFavorite(){
        return _apartmentDetails.get(Data.FAVORITE).getIntValue() == 1;
    }

    public int getGivenPrice(){
        return _apartmentDetails.get(Data.GIVEN_PRICE).getIntValue();
    }

    public int getCalcPrice(){
        return _apartmentDetails.get(Data.CALC_PRICE).getIntValue();
    }
}