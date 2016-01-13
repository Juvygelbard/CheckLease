package data;
import Formula.Formula;


/**
 * Created by user on 30/12/2015.
 */
public class Field {

    private int _id;
    private String _name;
    private int _type;
    private int _ex1;
    private String _ex2;
    private Formula _formula;

    public Field(int id, String name, int type, int ex1, String ex2, String formula){
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
    public int getEx1(){
        return _ex1;
    }
    public String getEx2(){
        return _ex2;
    }
    public int calculate(int X, int Y){
        return _formula.toSolve(X, Y);
    }

}
