package Formula;

import java.util.Vector;

public class Formula extends Token {
	private Vector<Token> _tokenized;
	
	private Formula() {
		super(TokenType.FORMULA);
		_tokenized = new Vector<Token>();
	}
	
	private static Vector<Token> Tokenize(String formula){
		Vector<Token> tokenized = new Vector<Token>();
		for(int i=0; i<formula.length(); i++){
			switch(formula.charAt(i)){
				case '+': 
					tokenized.add(new Token(TokenType.ADD));
					break;
				case '-':
					tokenized.add(new Token(TokenType.SUBTRACT));
					break;
				case '/':
					tokenized.add(new Token(TokenType.DEVIED));
					break;
				case '*':
					tokenized.add(new Token(TokenType.MULTIPLY));
					break;
				case '(':
					if((tokenized.size() > 0) && (
							 (tokenized.lastElement().getType() == TokenType.NUMBER) ||
							 (tokenized.lastElement().getType() == TokenType.CLOSE_B) ||
							 (tokenized.lastElement().getType() == TokenType.X) ||
							 (tokenized.lastElement().getType() == TokenType.Y) ))
						tokenized.add(new Token(TokenType.MULTIPLY));
					tokenized.add(new Token(TokenType.OPEN_B));
					break;
				case ')':
					tokenized.add(new Token(TokenType.CLOSE_B));
					break;
				case 'X':
					if((tokenized.size() > 0) && (
							 (tokenized.lastElement().getType() == TokenType.NUMBER) ||
							 (tokenized.lastElement().getType() == TokenType.CLOSE_B) ||
							 (tokenized.lastElement().getType() == TokenType.X) ||
							 (tokenized.lastElement().getType() == TokenType.Y) ))
						tokenized.add(new Token(TokenType.MULTIPLY));
					tokenized.add(new Token(TokenType.X));
					break;
				case 'Y':
					if((tokenized.size() > 0) && (
							 (tokenized.lastElement().getType() == TokenType.NUMBER) ||
							 (tokenized.lastElement().getType() == TokenType.CLOSE_B) ||
							 (tokenized.lastElement().getType() == TokenType.X) ||
							 (tokenized.lastElement().getType() == TokenType.Y) ))
						tokenized.add(new Token(TokenType.MULTIPLY));
					tokenized.add(new Token(TokenType.Y));
					break;
				default:
					if((formula.charAt(i) >= '0') && (formula.charAt(i) <= '9')){
						if((tokenized.size() > 0) && (
								 (tokenized.lastElement().getType() == TokenType.CLOSE_B) ||
								 (tokenized.lastElement().getType() == TokenType.X) ||
								 (tokenized.lastElement().getType() == TokenType.Y) ))
							tokenized.add(new Token(TokenType.MULTIPLY));
						if((tokenized.size() > 0) && (tokenized.lastElement().getType() == TokenType.NUMBER)){
							tokenized.lastElement().SetValue(tokenized.lastElement().getValue() * 10 + (formula.charAt(i) - 48));
						}
						else{
							tokenized.add(new Token(formula.charAt(i) - 48));
						}
					}
					break;
			}
		}
		// deriving inn-bracket expressions
		for(int i=0; i<tokenized.size(); i++){
			if(tokenized.get(i).getType() == TokenType.CLOSE_B){
				tokenized.remove(i);
				i--;
				Formula inner_exp = new Formula();
				while((i>=0) && (tokenized.get(i).getType() != TokenType.OPEN_B)){
					inner_exp._tokenized.add(0, tokenized.get(i));
					tokenized.remove(i);
					i--;
				}
				tokenized.remove(i);
				tokenized.add(i, inner_exp);
			}
		}
		return tokenized;
	}
	
	public String toString(){
		String formula = "";
		for(int i=0; i<_tokenized.size(); i++){
			formula = formula + _tokenized.get(i).toString();
		}
		return "(" + formula + ")";
	}
	
	private static boolean CheckFormula(String formula){
		// check for illigual ascii chars.
		for(int i=0; i<formula.length(); i++){
			if(!( (formula.charAt(i)>='0' && formula.charAt(i)<='9') ||
					(formula.charAt(i)=='+') ||
					(formula.charAt(i)=='-') ||
					(formula.charAt(i)=='*') ||
					(formula.charAt(i)=='/') ||
					(formula.charAt(i)=='(') ||
					(formula.charAt(i)==')') ||
					(formula.charAt(i)=='X') ||
					(formula.charAt(i)=='Y') ))
				return false;
		}
		
		// check for wrong syntax.
		for(int i=1; i<formula.length(); i++){
			if( ( (formula.charAt(i)=='+' || formula.charAt(i)=='-' || formula.charAt(i)=='*' || formula.charAt(i)=='/') &&
				  (formula.charAt(i-1)=='+' || formula.charAt(i-1)=='-' || formula.charAt(i-1)=='*' || formula.charAt(i-1)=='/') ) ||
				( (formula.charAt(i-1)=='(' &&
				  (formula.charAt(i)=='+' || formula.charAt(i)=='*' || formula.charAt(i)=='/') ) ) )
				return false;
		}
		
		return true;
	}
	
	public static Formula makeFormula(String formula){
		if(!CheckFormula(formula))
			throw new java.lang.ArithmeticException("'" + formula +"' is not a legal formula.");
		
		Formula new_formula = new Formula();
		new_formula._tokenized = Tokenize(formula);
		return new_formula;
	}
	
	private Token solve(int X, int Y){
		// replace X and Y tokens with Number tokens
		Vector<Token> to_resolve = new Vector<Token>();
		for(int i=0; i<_tokenized.size(); i++){
			if(_tokenized.get(i).getType() == TokenType.X)
				to_resolve.add(new Token(X));
			else if(_tokenized.get(i).getType() == TokenType.Y)
				to_resolve.add(new Token(Y));
			else
				to_resolve.add(_tokenized.get(i).clone());
		}
			
		// resolve all inner formulas
		for(int i=0; i<to_resolve.size(); i++){
			if(to_resolve.get(i).getType() == TokenType.FORMULA)
				to_resolve.set(i, ((Formula)to_resolve.get(i)).solve(X, Y));
		}
		
		// get-rid of minuses
		if(to_resolve.get(0).getType() == TokenType.SUBTRACT){
			to_resolve.remove(0);
			to_resolve.set(0, new Token(to_resolve.get(0).getValue() * -1));
		}
		
		// resolve for Multiply, Devide.
		for(int i=1; i<to_resolve.size()-1; i++){
			if(to_resolve.get(i).getType() == TokenType.MULTIPLY){
				int res = to_resolve.get(i-1).getValue() * to_resolve.get(i+1).getValue();
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.add(i-1, new Token(res));
				i = i - 2;
			}
			else if(to_resolve.get(i).getType() == TokenType.DEVIED){
				int res = to_resolve.get(i-1).getValue() / to_resolve.get(i+1).getValue();
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.add(i-1, new Token(res));
				i = i - 2;
			}	
		}
		
		// resolve for Add, Subtract.
		for(int i=1; i<to_resolve.size()-1; i++){
			if(to_resolve.get(i).getType() == TokenType.ADD){
				int res = to_resolve.get(i-1).getValue() + to_resolve.get(i+1).getValue();
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.add(i-1, new Token(res));
				i = i - 2;
			}
			else if(to_resolve.get(i).getType() == TokenType.SUBTRACT){
				int res = to_resolve.get(i-1).getValue() - to_resolve.get(i+1).getValue();
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.remove(i-1);
				to_resolve.add(i-1, new Token(res));
				i = i - 2;
			}	
		}
		return to_resolve.get(0);
	}

	public int toSolve(int X , int Y){
		Token ans = this.solve(X, Y);
		return ans.getValue();
	}
	// OVERRIDE
	public Token clone(){
		Formula ID = new Formula();
		for(int i=0; i<_tokenized.size(); i++){
			ID._tokenized.addElement(this._tokenized.get(i).clone());
		}
		return ID;
	}

}
