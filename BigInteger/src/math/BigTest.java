package math;

import java.io.IOException;
import java.util.Scanner;

public class BigTest {

	static Scanner sc;
	
	public static void parse() 
	throws IOException {
		System.out.print("\tEnter integer => ");
		sc.nextLine();
		String integer = sc.nextLine();
		try {
			BigInteger bigInteger = BigInteger.parse(integer);
			System.out.println("\t\tValue = " + bigInteger);
		} catch (IllegalArgumentException e) {
			System.out.println("\t\tIncorrect Format");
		}
	}
	
	public static void add() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		sc.nextLine();
		String integer = sc.nextLine();
		BigInteger firstBigInteger = BigInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		integer = sc.nextLine();
		BigInteger secondBigInteger = BigInteger.parse(integer);
		
		System.out.println("\t\tSum: " + firstBigInteger.add(secondBigInteger));
	}
	
	public static void multiply() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		sc.nextLine();
		String integer = sc.nextLine();
		BigInteger firstBigInteger = BigInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		//sc.nextLine();
		integer = sc.nextLine();
		BigInteger secondBigInteger = BigInteger.parse(integer);
		
		System.out.println("\t\tProduct: " + firstBigInteger.multiply(secondBigInteger));
		
	}
	
	public static void main(String[] args) 
	throws IOException {
		
		// TODO Auto-generated method stub
		sc = new Scanner(System.in);
		
		
		
		System.out.println("Testing multiplication");
		for(int l = -1000; l <= 1000; l++) {
			int i = l;
			for(int k = -1000; k <= 1000; k++) {
				int j = k;
				BigInteger one = BigInteger.parse(i + "");
				BigInteger two = BigInteger.parse(j + "");
				BigInteger bigInt = one.multiply(two);
				String answer = bigInt + "";
				int actualAns = i*j;
				String actual = "" + actualAns;
				
				//Simple check whether the answers match
				if(!actual.equals(answer)) {
					System.out.println("Your answer of " + answer 
							+ " does not match the actual answer of " + actual
							+ " (" + i + ", " + j + ")");
				}
				
				//Make sure the 'negative' field of answer is correct
				if(actual.length() > 0 && actual.charAt(0) == '-') {
					actual = actual.substring(1); //truncate the sign for checking numDigits later
					//If the answer is negative but bigInt.negative is false, that is incorrect
					if(bigInt.negative == false) {
						System.out.println("Negatives don't match. Calculated: " + bigInt.negative + ", actual: true");
					}
				} else {
					//If the answer is positive but bigInt.negative is true, that is incorrect
					if(bigInt.negative == true){
						System.out.println("Negatives don't match. Calculated: " + bigInt.negative + ", actual: false");
					}
				}
				
				//Check if the number of digits matches
				if(bigInt.numDigits != actual.length()) {
					//The only time when they shouldn't match is when answer is 0 (so the string length would 
					// be one, whereas the actual numDigits should be zero)
					//If this is the case, make sure that front is null AND numDigits is 0.
					if(bigInt.front != null || bigInt.numDigits != 0) {
						System.out.println("Null: " + (bigInt.front == null));
						System.out.println("Your number of digits " + bigInt.numDigits + " does not equal " + actual.length());
						System.out.println("Your answer of " + answer 
								+ " does not match the actual answer of " + actual
								+ " (" + i + ", " + j + ")");
					}
				}
			}
		}
		
		

		System.out.println("Testing addition");
		for(int l = -1000; l <= 1000; l++) {
			int i = l;
			for(int k = -1000; k <= 1000; k++) {
				int j = k;
				BigInteger one = BigInteger.parse(i + "");
				BigInteger two = BigInteger.parse(j + "");
				BigInteger bigInt = one.add(two);
				String answer = bigInt + "";
				int actualAns = i+j;
				String actual = "" + actualAns;
				
				//Simple check whether the answers match
				if(!actual.equals(answer)) {
					System.out.println("Your answer of " + answer 
							+ " does not match the actual answer of " + actual
							+ " (" + i + ", " + j + ")");
				}
				
				//Make sure the 'negative' field of answer is correct
				if(actual.length() > 0 && actual.charAt(0) == '-') {
					actual = actual.substring(1); //truncate the sign for checking numDigits later
					//If the answer is negative but bigInt.negative is false, that is incorrect
					if(bigInt.negative == false) {
						System.out.println("Negatives don't match. Calculated: " + bigInt.negative + ", actual: true");
					}
				} else {
					//If the answer is positive but bigInt.negative is true, that is incorrect
					if(bigInt.negative == true){
						System.out.println("Negatives don't match. Calculated: " + bigInt.negative + ", actual: false");
					}
				}
				
				//Check if the number of digits matches
				if(bigInt.numDigits != actual.length()) {
					//The only time when they shouldn't match is when answer is 0 (so the string length would 
					// be one, whereas the actual numDigits should be zero)
					//If this is the case, make sure that front is null AND numDigits is 0.
					if(bigInt.front != null || bigInt.numDigits != 0) {
						System.out.println("Null: " + (bigInt.front == null));
						System.out.println("Your number of digits " + bigInt.numDigits + " does not equal " + actual.length());
						System.out.println("Your answer of " + answer 
								+ " does not match the actual answer of " + actual
								+ " (" + i + ", " + j + ")");
					}
				}
			}
		}
		
		
		
		char choice;
		while ((choice = getChoice()) != 'q') {
			switch (choice) {
				case 'p' : parse(); break;
				case 'a' : add(); break;
				case 'm' : multiply(); break;
				default: System.out.println("Incorrect choice"); 
			}
		}
	}

	private static char getChoice() {
		System.out.print("\n(p)arse, (a)dd, (m)ultiply, or (q)uit? => ");
		String in = sc.next();
		char choice;
		if (in == null || in.length() == 0) {
			choice = ' ';
		} else {
			choice = in.toLowerCase().charAt(0);
		}
		return choice;
	}

}
