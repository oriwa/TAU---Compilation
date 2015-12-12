package slp;

public class Validator {
	
	public static void handleSemanticError(String error, int line) {
		System.out.println("ERROR in line +" + line + ": " + error);
		System.exit(1);
	}
	
	public static void validateIlegalOp(TypeEntry lType, TypeEntry rType, Operator op, int line,Environment env) {
		String errorV1="The operator " +op.toString() + " is undefined for types " +lType.getEntryName()+", "+rType.getEntryName();
		String errorV2="incompatible operand types "+lType.getEntryName()+" and "+rType.getEntryName();
		

		boolean matchingTypes=env.isA(lType, rType)||env.isA(rType, lType);
		
		TypeEntry expected;
		
		switch (op){
		case PLUS:
			expected=env.getTypeEntry(Environment.STRING);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return; //all okay, string+string
			}
			//else continue as an ordinarymathematical operation
		case MINUS:
		case MULTIPLY:
		case DIVIDE:
		case MOD: 
			
			expected=env.getTypeEntry(Environment.INT);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return;	//all okay, int (mathOp) int
			}
			handleSemanticError(errorV1, line);
		case LT:
		case GT:
		case LTE:
		case GTE: 
			expected=env.getTypeEntry(Environment.INT);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return; //all okay, int (comparingOp) int
			}
			handleSemanticError(errorV1, line);
		case EQUAL:
		case NEQUAL:
			if(matchingTypes){
				return;	//all okay, equality between matching types (or subtypes)
			}
			handleSemanticError(errorV2, line);
		case LAND:
		case LOR:
			expected=env.getTypeEntry(Environment.BOOLEAN);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return; // all okay, boolean (logicalOp) boolean
			}
			handleSemanticError(errorV1, line);
		case LNEG: /*should never get here: error in parsing!*/
			
			handleSemanticError("oops! compiler error in parsing, parsed '!' as binary op", line);
			/*TODO better handling error for debugging*/
		}

		
	}

	public static void validateIlegalOp(TypeEntry type, Operator op, int line,Environment env) {
		String error="The operator " +op.toString() + " is undefined for types " +type.getEntryName();
		
		if(op==Operator.MINUS){
			if(type.getEntryId()!=env.getTypeEntry(Environment.INT).getEntryId()){
				handleSemanticError(error, line);
			}
			return; //all okay, -int
		}
		else if(op==Operator.LNEG){
			if(type.getEntryId()!=env.getTypeEntry(Environment.BOOLEAN).getEntryId()){
				handleSemanticError(error, line);
			}
			return; //all okay, !bool			
		}
		/*should never get here: error in parsing!*/
		
		handleSemanticError("oops! compiler error in parsing, parsed "+op.toString()+" as unary op", line);
		/*TODO better handling error for debugging*/	
	}
	

	public static void validateTypeMismatch(TypeEntry expectedType,
			TypeEntry actualType, int line,Environment env) {

		if (!isDimensionEqual(expectedType, actualType)|| !env.isA(expectedType, actualType)){
			handleSemanticError(
							"type mismatch: cannot convert from "
							+ actualType.getEntryName() + " to "
							+ expectedType.getEntryName(), line);
		}

	}

	public static void validateTypeMismatch(String expectedTypeName, Integer actualTypeId, int line,Environment env) {
			validateTypeMismatch(env.getTypeEntry(expectedTypeName),
				env.getTypeEntry(actualTypeId), line,env);
	}

	private static boolean isDimensionEqual(TypeEntry type1, TypeEntry type2) {
		return type1.getTypeDimension() == type2.getTypeDimension();
	}


	public static void validateIsArray(TypeEntry potentialArray, int line) {
		if (potentialArray.getTypeDimension() == 0) {
			handleSemanticError("length cannot be resolved or is not a field",	line);
		}
	}

	public static void validateUndefRef(String name,int line, Environment env) {
		// TODO Auto-generated method stub
		
	}

}
