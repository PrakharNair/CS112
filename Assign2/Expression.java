package apps;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	//A scalar and an array which I can use to manipulate info here.
    	
	    Stack<String> symb= new Stack<String>();
	    //Evidence of implementation of stacks.
	    /* The reason I will use stacks here is because it will be much easier to keep track of the symbols
	     * With a stack rather than using a queue, or an arraylist.I was thinking that it might even be possible to use
	     * an arraylist,but this seems to be the best course of action.
	     *
	     * Most of the stack usage is within the is to find the tokens, or set token to a new value
	     */
	
	    StringTokenizer s = new StringTokenizer(expr,delims,true);
	    //Allows us to manipulate the token
	
	    String token = "";
	    //token
	
	    while(s.hasMoreTokens()){
	    	//checks for tokens. This is a method from the StringTokenizer class.
	    	token = s.nextToken();
	    	if((token.charAt(0) >= 'a' && token.charAt(0)<='z')||(token.charAt(0) >= 'A'&&token.charAt(0) <= 'Z'||token.equals("["))){
	    	symb.push(token);
	    	//Push is a statement in the stack class that pushes the item to the top of the stack.
	    	}//end if
	    	//This if statement checks if the token exists within the alphabet, or if a bracket is found.
	    	/*
	    	*The reasoning behind this is basically a safecheck, which will allow the symbol to be pushed to the top of the stack.
	    	*/
	    }//endwhile

		while(!symb.isEmpty()) {
			token = symb.pop();
			//Pops item at top of stack and returns it.
			if(token.equals("[")) {
				//Looks for opening bracket
				token = symb.pop();
				//pops again
				ArraySymbol arrSymb = new ArraySymbol(token);
				//if there is a bracket the symbol will be treated as an array element.
				if(arrays.indexOf(arrSymb) == -1) {
					arrays.add(arrSymb);
				}//end if
				//Adds to the array
			}//end if 
			else {
				//if there is no bracket, that means it is a scalar, and has to be treated as such.
				ScalarSymbol scalSymb = new ScalarSymbol(token);
				//creates a new scalar symbol
				if(scalars.indexOf(scalSymb) == -1) {
					scalars.add(scalSymb);
				}//end if
			}//end else
		}//end while
    }//end while


    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { //scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    

	private ArraySymbol arr(String name) {
		//checks for the value in the array
		for (int n = 0; n < arrays.size(); n++) {
			//looks through the array
			if (arrays.get(n).name.equals(name)){
				//finds the value and returns it
				return arrays.get(n);
			}
		}
		return null;
	}
	/*The reason this array will be used is to do the math within the array, for example of etest2. 
	 * This method is necesarry and i had to add it last minute. 
	 * 
	 */

	private boolean mult(char c) {
		return c == '*';
	}
	
	/*
	 * These 4 will concat the the operators into strings so that pemdas can exist.
	 */
	
	private boolean div(char c) {
		return c == '/';
	}//end div

	private boolean add(char c) {
		return c == '+';
	}//end add

	private boolean sub(char c) {
		return c == '-';
	}//end sub
    
	
	//Method that will check for boolean. Used once in the eval method.
	private boolean isBrack(char opBrack) {
		if (opBrack == '(') {
			return true;
		}//end if
		else if (opBrack == '[') {
			return true;
		}//end else if
		else {
			return false;
		}//end else
	}//end isBrack
	
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
	
    public float evaluate() {
		for(int n = 0; n < this.scalars.size(); n++) {
			//checks scalars 
			expr = expr.replace(this.scalars.get(n).name, "" + this.scalars.get(n).value);
			//Sets expression to an expression that can be used in the next evaluate method
		}//end for
		expr = this.evaluate(expr);
		return Float.parseFloat(expr);
    }
    
	private String evaluate(String expr) {		
		if(expr == null || expr.length() == 0) {
			return "0";
		}//end if
		//checks for null
		
		if(expr.indexOf('[') == -1) {
			//looks for bracket
			if(expr.indexOf('(') == -1) {
				//looks for parenthesis
				expr = expr.replace(" ", "");
				//Gets rid of spaces that are found throughout the string
				//Had to add this line after using test cases given on sakai
				expr = " " + expr;
				//adds a space at the start of the expression to allow us to work with items. 

				float first, second;
				//First number and second number set as float
				
				int index1, index2;
				//First index and second indexs of the first number and second number, set as ints.
				
				boolean isNeg = false;
				//Checks for negative, starting value is false, because the original value is false. 

				int count = 0;
				//Count set as an int for the operator counters

				for(int n = 2; n < expr.length(); n++) {
					/*Count the operators, assuming that since a space is added, and a number exists, or the negative sign exists, 
					 * we cannot set n = 0. This would find the "-" as a sub, which will add to the count incorrectly. Theoretically 
					 * a operator can only exist at or after the third spot. 
					 */
					
					
					if(this.add(expr.charAt(n))) {
						count++;
						//looks for +
					}//end if
					
					if(this.sub(expr.charAt(n))) {
						count++;
						//looks for - 
					}//end if
					
					if(this.mult(expr.charAt(n))) {
						count++;
						//looks for *
					}//end if
					
					if(this.div(expr.charAt(n))) {
						count++;
						//looks for /
					}//end if
				}//end for
				
				if(count == 0) {
					return expr;
					//this means that there are no operators, and there are no variables, the answer is simply the expression
				}//end if
				
				for(int n = 0; n < expr.length(); n++) {
					if(n == 1 && this.sub(expr.charAt(n))) {
						isNeg = true;
						//checks for negative
					}//end if
					
					else if(this.mult(expr.charAt(n)) || this.div(expr.charAt(n))) {
						//looks for multiplication and division first
						index2 = n;
						index1 = index2;
						//sets indexs = n. 
						
						while(index1 > 0 && (Character.isDigit(expr.charAt(index1-1)) || expr.charAt(index1-1) == '.')) {
							index1--;
							//Pinpoints the index of the first. 
							//The first part is necesarry because eventually this loop can make index lower than 0, which would cause an error. 
						}//end while

						while(index2 < expr.length() - 1 && (Character.isDigit(expr.charAt(index2 + 1)) || expr.charAt(index2 + 1) == '.')) {
							//pinpoints the index of the second number
							index2++;
						}//end while

						try {
							first = Float.parseFloat(expr.substring(index1, n));
							//Sets a value to first dependant on the initial index1, that was found in the while loop iterations
						}//end try
						catch(Exception e) {
							first = 0;
							//Exception e will be filled if the try is unsucessful, and will state that the first number is 0
						}//end exception catch

						try {
							second = Float.parseFloat(expr.substring(n + 1, index2 + 1));
							//Sets a value to second dependent on the initial index2, that was found in the while loop iterations
						}//end try
						catch(Exception e) {
							second = 0;
							//Exception e will be filled if the try is unsuccessful, and will state that the second number is 0
						}//end exception catch

						String f = "";
						//creates a string that is basically the final line after all the operators are found. 
						
						if(isNeg == true) {
							first = -1 * first;
							//makes the first number negative
						}//end if

						if(this.mult(expr.charAt(n))) {
							f = "" + first * second;
							//does multiplication

						}//end if
						
						else {
							f = "" + first / second;
							//does division
						}//end else

						int s = 0;
						//An integer that acts as another index for string
						
						if(isNeg == true) {
							s = 2;
							//If the answer is negative, accounting the space and the negative, the first number starts at index 2.
						}//end if
						expr = expr.substring(s, index1) + f + expr.substring(index2+ 1);
						//Sets the expression to a new value based on what was done in the previous while and for loops. 
						n = 0;
						//reset n to 0
						isNeg = false;
						//reset isNeg to false. 
					}//end else if 
				}//end for
				
				isNeg = false;
				
				for(int n = 0; n < expr.length(); n++) {
					if(n == 1 && this.sub(expr.charAt(n))) {
						isNeg = true;
						//shows that the there is a negative.
					}//end if 
					
					else if(this.add(expr.charAt(n)) || this.sub(expr.charAt(n))) {
						index2 = n;
						//index of the second number
						index1 = index2;
						//index of the first number is set to the index of the second number

						while(index1 > 0 && (Character.isDigit(expr.charAt(index1 - 1)) || expr.charAt(index1 - 1) == '.')) {
							index1--;
							//Pinpoints the index of the first. 
							//The first part is necesarry because eventually this loop can make index lower than 0, which would cause an error.
						}//end while

						while(index2 < expr.length() - 1 && (Character.isDigit(expr.charAt(index2 + 1)) || expr.charAt(index2 + 1) == '.')) {
							index2++;
							//pinpoints the index of the second number
						}//end while

						try {
							first = Float.parseFloat(expr.substring(index1, n));
							//Sets a value to first dependent on the initial index1, that was found in the while loop iterations
						}//end try 
						catch(Exception e) {
							first = 0;
							//Exception e will be filled if the try is unsuccessful, and will state that the first number is 0
						}//end catch
						
						try {
							second = Float.parseFloat(expr.substring(n + 1, index2 + 1));
							//Sets a value to second dependent on the initial index1, that was found in the while loop iterations
						}//end try 
						catch(Exception e) {
							//Exception e will be filled if the try is unsuccessful, and will state that the second number is 0
							second = 0;
						}//end catch

						String f = "";
						// a static string that is used to piece the answer together
						
						if(isNeg == true) {
							first = -1 * first;
							//sets it to negative
						}//end if

						if(this.add(expr.charAt(n))) {
							f = "" + (first + second);
							//addition
						}//end if
						
						else {
							f = "" + (first - second);
							//subtraction
						}//end else

						int s = 0;
						if (isNeg == true) {
							s = 2;
							//index of negative number 
						}//end if

						expr = expr.substring(s, index1) + f + expr.substring(index2+1);
						//sets new expression. 
						/*s, index1 is to make sure that if there is a negative, the new index is put in
						 * 
						 * f is the usage of subtraction/addition
						 * 
						 * index2 + 1 is to show the end
						 */

						n = 0;
						isNeg = false;
						//reset the values
					}//end else if
				}//end for
				return expr;
			}//end if
			
			else {
				//if there is a bracket
				for(int i = 0; i < expr.length(); i++){
					//checks through the expression
					if(this.isBrack(expr.charAt(i)) == true) {
						//calls isBracket expression and makes sure there is one
						int x = i;
						//x is set as an index for the beginning
						int count = 1;
						//counter
						i++;
						//increments to avoid stackoverflow error	
						int y = i;
						//sets y outside the for loop because the variable will be used outside te loop
						for(; y < expr.length(); y++) {
							if(expr.charAt(y) == '(') {
								count++;
								//checks for the opening bracket
							}//end if
							
							if(expr.charAt(y) == ')') {
								count--;
								//checks for the closing bracket
							}//end if
							
							if(count == 0 && expr.charAt(y) == ')') {
								break;
								//ends the loop after the last closing bracket is found
							}//end if
						}//end for

						String beg = expr.substring(0, x);
						String mid = this.evaluate(expr.substring(i, y));
						String last = expr.substring(y + 1);
						//This will piece together the string
						
						/*RECURSION*/
						return this.evaluate(beg + mid + last);
						//reruns the code based with these inputs
					}//end if
				}//end for
			}//end else
		}//end if
		
		else {
			//if there are brackets
			for(int n = 0; n < expr.length(); n++){
				//checks entire expression
				if(Character.isLetter(expr.charAt(n))) {
					String curr = "";
					//sets a current variable
					boolean isArr = false;
					//checks to make sure if an array is being entered. 
					
					int x = n;
					//Sets an index
					for(; n < expr.length() && Character.isLetter(expr.charAt(n)); n++) {
						//checks the index
						curr = curr + expr.charAt(n);
						//adds the character that is at the index
					}//end while
					
					if(n < expr.length() && expr.charAt(n) == '[') {
						isArr = true;
						//confirms that it is an array
					}//end if

					if(isArr = true) {
						//if it is an array
						int count = 1;
						//sets a counter
						n++;
						//increments n
						int y = n;
						//sets an index
						for(; y < expr.length(); y++) {
							if(expr.charAt(y) == '[') {
								count++;
								//looks for the opening bracket
							}//end if
							
							if(expr.charAt(y) == ']') {
								count--;
								//loks for the closing bracket
							}//end if

							if(count == 0 && expr.charAt(y) == ']') {
								break;
								//once the final ending bracket is found, the loop is over
							}//end if
						}//endfor
						
						String beg = expr.substring(0, x);
						//first part of the expression
						String mid = "" + this.arr(curr).values[(int)Float.parseFloat(this.evaluate(expr.substring(n, y)))];
						/* Second part of the equation which uses recursion. this is to ensure that it will go through as many as times needed 
						 * using the new indexes.  Since it uses an array it must first find the index of the array,
						 * then concat the float that was used previously as value to an int, so it can be used recursively in evaluate
						 * then it runs the parseFloat method, and using the new indexes, finds the middle
						 * 
						 */
						
						String end = expr.substring(y+1);
						//last part of the expression
						
						/*RECURSION*/
						return this.evaluate(beg + mid + end);
						//It will keep running this in case there are more brackets, or are nested brackets that adhere to pemdas
						
					}//end if
				}//end if
			}//end for
		}//end else
		return "";
		//Since there is recursion, this will print nothing at the end
	}//end evaluate

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
