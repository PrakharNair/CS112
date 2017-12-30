package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		if(poly == null || p.poly == null) {
			System.out.println("Empty polynomial");
			Polynomial nullPoly = new Polynomial();
			nullPoly.poly = new Node (0, 0, null);
			return nullPoly;
		} //Null pointer exception
		
		Node curr = poly;
		Node polyCurr = p.poly;
		//Two nodes to work with
		
		Polynomial pSum = new Polynomial();
		Node temp = null;
		float currCoeff;
		
		//These three values will be manipulated by the input, and ultimately give us the final answer.
		//Notice, there is no int currDeg, because in "Add", I'm not changing the degree. In multiply, I will. 
		
		while(curr != null) {	
			if(polyCurr == null) {
				temp = new Node(curr.term.coeff, curr.term.degree, temp);
				curr = curr.next;
			}//end if
			/* This if statement will create a new polynomial if polyCurr is empty, and move the curr pointer down
			 * This will push it down until the entire node is finished being examined.
			 */
			
			else if(curr.term.degree == polyCurr.term.degree) {
				currCoeff = curr.term.coeff + polyCurr.term.coeff;
				temp = new Node(currCoeff, curr.term.degree, temp);
				curr = curr.next;
				polyCurr = polyCurr.next;
			}//end if
			//This if statement will match the degree in the polynomial. 
			
			else if(curr.term.degree < polyCurr.term.degree) {
				temp = new Node(curr.term.coeff, curr.term.degree, temp);
				curr = curr.next; 
			}//end if
			/* This if will be true if the the curr polynomial degree is less than the polyCurrs.
			 * This will push the curr pointer down one and reset the while loop. The goal of this is to find the scenario
			 * where the degrees are equal and match the else if previous to this one.
			 */
			else if(curr.term.degree > polyCurr.term.degree) {
				temp = new Node(polyCurr.term.coeff, polyCurr.term.degree, temp);
				polyCurr = polyCurr.next;
			}//end if
			//Same scenario as above, except polyCurr's degree is less than curr. 
		}//end while
		/* The purpose of this while loop is to do most of the computations.
		 * However, and error will arise. I found this when testing ptest1 + ptest2.
		 * There is an empty value for x^4 in ptest 1, but ptest2 has a value for that degree. 
		 * Basically, if there are nulls in degrees for polyCurr, this while loop cannot check for it.
		 * The only way to fix that is to create a new while loop, that checks polyCurr. 
		 * 
		 * Another while loop must be added to fix this issue.
		 * Missing this might cause issues within the multiply method as well.
		 */
		
		//This while loop's logic is very similar to the previous one's, so for comments and documentation, check the previous loop.
		while(polyCurr != null) {
			if(curr == null) {
				temp = new Node(polyCurr.term.coeff, polyCurr.term.degree, temp);
				polyCurr = polyCurr.next;
			}//end if
			
			else if(curr.term.degree == polyCurr.term.degree) {
				currCoeff = curr.term.coeff + polyCurr.term.coeff;
				temp = new Node(currCoeff, curr.term.degree, temp);
				curr = curr.next;
				polyCurr = polyCurr.next;

			}//end if
			
			else if(curr.term.degree < polyCurr.term.degree) {
				temp = new Node(curr.term.coeff, curr.term.degree, temp);
				curr = curr.next;
			}//end if
			
			else if(curr.term.degree > polyCurr.term.degree) {
				temp = new Node(polyCurr.term.coeff, polyCurr.term.degree, temp);
				polyCurr = polyCurr.next;
			}//end if
		}//end while
		
		/* Now, the issue that arises is that the polynomial is not ordered.
		 * To order it, I must use a while loop.
		 */
		
		Node order = temp;
		Node orderTemp = null;
		
		while(order != null) {
			if(order.term.coeff == 0) {
				order = order.next;
			}//end if
			else {
				orderTemp = new Node(order.term.coeff, order.term.degree, orderTemp);
				order = order.next;
			}//end else
			
		}//end order while
		
		pSum.poly = orderTemp;
		return pSum;
	}
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		if(poly == null || p.poly == null) {
			System.out.println("Empty polynomial");
			Polynomial nullPoly = new Polynomial();
			nullPoly.poly = new Node (0, 0, null);
			return nullPoly;
		} //Null pointer exception
		
		Node curr = poly;
		Node polyCurr = p.poly;
		
		float currCoeff; 
		int currDeg;

		/* Set two nodes that have values to work with.
		 * I also have another idea of having 3 different nodes, setting one of them equal to null.
		 * 
		 * I feel as though working this way may be slightly easier to use, however, by doing that, I may not be able to 
		 * find the correct solution. Therefore, I will work on that method after this assignment is turned in.
		 */
		
		Node temp = null;
		Polynomial pTemp = new Polynomial();
		Polynomial pSum = new Polynomial();
		//These are the values that will be changed by inputs, and used in the following while loop
		
		while(curr != null) {
	
			while(polyCurr != null) {
				currCoeff = curr.term.coeff * polyCurr.term.coeff;
				currDeg = curr.term.degree + polyCurr.term.degree;
				//This will use the input polynomial and multiply it with the next polynomial the user inputs
				temp = new Node(currCoeff, currDeg, temp);
				//Set a new temp node that houses the values of the multiplied coeff/degs
				polyCurr = polyCurr.next;
				//make polyCurr go to the next value in the node
			}//end polyCurr while
			/* While this mathematically does work, the polynomial will print in reverse order.
			 * To fix that, I'll add another while loop.
			 * NOTE THIS IS  THE SAME AS THE LOOP IN THE "ADD" METHOD.
			 */
			
			Node order = temp;
			Node orderTemp = null;
			
			while(order != null) {
				if(order.term.coeff == 0) {
					order = order.next;
				}//end if
				else {
					orderTemp = new Node(order.term.coeff, order.term.degree, orderTemp);
					order = order.next;
				}//end else
				
			}//end order while
			//This while loop will correctly order every value in the polynomial
			
			pTemp.poly = orderTemp;
			pSum = pSum.add(pTemp);
			
			curr = curr.next;
			polyCurr = p.poly;
			temp = null;
			
		}//end curr while
		return pSum;
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		float y = 0;
		Node curr = poly;
		//Two values to work with.
		
		while(curr != null) {
			y = y + curr.term.coeff * (float) Math.pow(x, curr.term.degree);
			/* When trying to write this line, I was getting an error if i didn't concat the value
			 * derived from the "Math" statement into a (float)
			 */
			curr = curr.next;
		} 
		/* This while loop will solve for the value of the input.
		 * The statement uses the "MATH" class which we learned about last year. I used the pow method.
		 * 
		 * The first issue was with a double being returned, but by concatenating it to a float, it will compile.
		 * It will solve for y until it reaches the end of the problem, and y is returned. 
		 */
		
		return y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
