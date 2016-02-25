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

    public Value(String strVal, int intVal){
        _strValue = strVal;
        _intValue = intVal;
    }

    public Value(Value toCopy){
        _strValue = toCopy._strValue;
        _intValue = toCopy._intValue;
    }

    public int getIntValue(){
        return _intValue;
    }

    public String getStrValue(){
        return _strValue;
    }
}