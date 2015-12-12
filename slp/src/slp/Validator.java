package slp;


public class Validator {
	
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
			env.handleSemanticError(errorV1, line);
		case LT:
		case GT:
		case LTE:
		case GTE: 
			expected=env.getTypeEntry(Environment.INT);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return; //all okay, int (comparingOp) int
			}
			env.handleSemanticError(errorV1, line);
		case EQUAL:
		case NEQUAL:
			if(matchingTypes){
				return;	//all okay, equality between matching types (or subtypes)
			}
			env.handleSemanticError(errorV2, line);
		case LAND:
		case LOR:
			expected=env.getTypeEntry(Environment.BOOLEAN);
			if(matchingTypes&&lType.getEntryId()==expected.getEntryId()){
				return; // all okay, boolean (logicalOp) boolean
			}
			env.handleSemanticError(errorV1, line);
		case LNEG: /*should never get here: error in parsing!*/
			
			env.handleSemanticError("oops! compiler error in parsing, parsed '!' as binary op", line);
			/*TODO better handling error for debugging*/
		}
		
	}

	public static void validateIlegalOp(TypeEntry type, Operator op, int line,Environment env) {
		String error="The operator " +op.toString() + " is undefined for types " +type.getEntryName();
		
		if(op==Operator.MINUS){
			if(type.getEntryId()!=env.getTypeEntry(Environment.INT).getEntryId()){
				env.handleSemanticError(error, line);
			}
			return; //all okay, -int
		}
		else if(op==Operator.LNEG){
			if(type.getEntryId()!=env.getTypeEntry(Environment.BOOLEAN).getEntryId()){
				env.handleSemanticError(error, line);
			}
			return; //all okay, !bool			
		}
		/*should never get here: error in parsing!*/
		
		env.handleSemanticError("oops! compiler error in parsing, parsed "+op.toString()+" as unary op", line);
		/*TODO better handling error for debugging*/	
	}

	public static void validateLibraryInstantiation(TypeEntry type, Environment env, int line) {
		if(type.getEntryId()==env.getTypeEntry("Library").getEntryId()){
			env.handleSemanticError("Cannot instantiate the type Library", line);
		}
		
	}
	
	public static void validateInitialized(VisitResult visitResult,int line,Environment env)
	{
		if(!visitResult.isInitialized)
			env.handleSemanticError("The local variable " +visitResult.uninitializedId+" may not have been initialized", line);
		
	}
	
}
