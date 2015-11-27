package slp;

/** An enumeration containing all the operation types in the SLP language.
 */
public enum Operator {
	MINUS, PLUS, MULTIPLY, DIVIDE, MOD, LT, GT, LTE, GTE, EQUAL,NEQUAL, LAND, LOR, LNEG;
	
	/** Prints the operator in the same way it appears in the program.
	 */
	public String toString() {
		switch (this) {
		case MINUS: return "subtraction";
		case PLUS: return "addition";
		case MULTIPLY: return "multiplication";
		case DIVIDE: return "division";
		case MOD: return "modulus";
		case LT: return "less than";
		case GT: return "greater than";
		case LTE: return "less than or equal to";
		case GTE: return "greater than or equal to";
		case EQUAL: return "equality";
		case NEQUAL: return "inequality";
		case LAND: return "conjunction";
		case LOR: return "disjunction";
		case LNEG: return "negation";
		default: throw new RuntimeException("Unexpted value: " + this.name());
		}
	}
}