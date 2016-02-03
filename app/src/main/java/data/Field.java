package data;
import Formula.Formula;
import java.util.ArrayList;

/**
 * Created by user on 30/12/2015.
 */
public class Field {
    public static final int CHECKBOX = 0;
    public static final int MULTISELECT = 1;
    public static final int NUMBER = 2;
    public static final int TEXT = 3;
    public static final int PHONE = 4;

    private int _id;
    private String _name;
    private int _type;
    private String _ex1;
    private int _ex2;
    private Formula _formula;

    public Field(int id, String name, int type, String ex1, int ex2, String formula){
        _id = id;
        _name = name;
        _type = type;
        _ex1 = ex1;
        _ex2 = ex2;
        _formula = Formula.makeFormula(formula);
    }
    public int getId(){
        return _id;
    }
    public String getName(){
        return _name;
    }
    public int getType(){
        return _type;
    }
    public String getEx1(){
        return _ex1;
    }
    public int getEx2(){
        return _ex2;
    }
    public int calculate(int X, int Y){
        return _formula.toSolve(X, Y);
    }

    public static int calculateFieldList(ArrayList<Value> params, ArrayList<Field> fields){
        if(params.size() != fields.size())
            throw new RuntimeException("Number of params must be the same as the number of fields!");
        int score = 0;
        for(int i=0; i< params.size(); i++){
            Field field = fields.get(i);
            Value param = params.get(i);
            score = field.calculate(score, param.getIntValue());;
        }
        return score;
    }
}
