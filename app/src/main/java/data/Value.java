package data;

/**
 * Created by user on 30/12/2015.
 */
public class Value {

    private String _strValue;
    private int _intValue;

    public Value(String value){
        _strValue = value;
        _intValue = 0;
    }

    public Value(int value){
        _strValue = "NULL";
        _intValue = value;
    }

    public int getIntValue(){
        return _intValue;
    }

    public String getStrValue(){
        return _strValue;
    }

    public String toString(){

        if(_strValue.equals("NULL"))
            return "Integer" + Data.LINE_SEPARATOR + _intValue;
        else
            return "String" + Data.LINE_SEPARATOR + _strValue;
    }
}