package slp;

import java.io.*;

import java_cup.runtime.*;

/** The entry point of the SLP (Straight Line Program) application.
 *
 */
public class Main {
	private static boolean printtokens = false;
	
	/** Reads an SLP and pretty-prints it.
	 * 
	 * @param args Should be the name of the file containing an SLP.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		try {
			if (args.length == 0) {
				System.out.println("Error: Missing input file argument!");
				printUsage();
				System.exit(-1);
			}
			if (args.length == 2) {
				if (args[1].equals("-printtokens")) {
					printtokens = true;
				}
				else {
					printUsage();
					System.exit(-1);
				}
			}
			
			// Parse the input file
			FileReader txtFile = new FileReader(args[0]);
			Lexer scanner = new Lexer(txtFile);
			Parser parser = new Parser(scanner);
			parser.printTokens = printtokens;
			
			Symbol parseSymbol = parser.parse();
			System.out.println("Parsed " + args[0] + " successfully!");
			Program root = (Program) parseSymbol.value;
			
			// Pretty-print the program to System.out
			PrettyPrinter printer = new PrettyPrinter(root);
			printer.print();
			
			// Interpret the program
			SLPEvaluator evaluator = new SLPEvaluator(root);
			evaluator.evaluate();
			
			//generate ir
			IRGenerator irGenerator = new IRGenerator(root);
			irGenerator.Generate();
			
//		} catch (Exception e) {
//			System.out.print(e);
//		}
	}
	
	/** Prints usage information about this application to System.out
	 */
	public static void printUsage() {
		System.out.println("Usage: slp file [-printtokens]");
	}
}