package Formula;

class Token implements Cloneable{
	private TokenType _type;
	private int _val;
	
	public Token(TokenType type){
		_type = type;
	}
	
	public Token(int value){
		_type = TokenType.NUMBER;
		_val = value;
	}
	
	public TokenType getType(){
		return _type;
	}
	
	public int getValue(){
		return _val;
	}
	
	public void SetValue(int new_val){
		_val = new_val;
	}
	
	public String toString(){
		switch(_type){
			case ADD:
				return "+";
			case SUBTRACT:
				return "-";
			case DEVIED:
				return "/";
			case MULTIPLY:
				return "*";
			case X:
				return "X";
			case Y:
				return "Y";
			default:
				return "" + _val;
		}
	}
	
	public Token clone(){
		Token ID = new Token(_type);
		ID._val = this._val;
		return ID;
	}
}