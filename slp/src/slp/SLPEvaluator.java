package slp;

import java.io.IOException;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, Integer> {
	protected ASTNode root;

	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public SLPEvaluator(ASTNode root) {
		this.root = root;
	}
	
	/** Interprets the AST passed to the constructor.
	 */
	public void evaluate() {
		Environment env = new Environment();
		root.accept(this, env);
	}
	
	public Integer visit(StmtList stmts, Environment env) {
		for (Stmt st : stmts.statements) {
			st.accept(this, env);
		}
		return null;
	}

	public Integer visit(Stmt stmt, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt!");
	}



	public Integer visit(AssignStmt stmt, Environment env) {
		//Expr rhs = stmt.rhs;
		//Integer expressionValue = rhs.accept(this, env);
		//AssignStmt var = stmt.location;
		//env.update(var, expressionValue);
		return null;
	}

	public Integer visit(Expr expr, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Expr!");
	}



	public Integer visit(VarExpr expr, Environment env) {
		return env.get(expr);
	}

	public Integer visit(NumberExpr expr, Environment env) {
		return new Integer(expr.value);		
		// return expr.value; also works in Java 1.5 because of auto-boxing
	}

	public Integer visit(UnaryOpExpr expr, Environment env) {
		Operator op = expr.op;
		if (op != Operator.MINUS)
			throw new RuntimeException("Encountered unexpected operator " + op);
		Integer value = expr.operand.accept(this, env);
		return new Integer(- value.intValue());
	}

	public Integer visit(BinaryOpExpr expr, Environment env) {
		Integer lhsValue = expr.lhs.accept(this, env);
		int lhsInt = lhsValue.intValue();
		Integer rhsValue = expr.rhs.accept(this, env);
		int rhsInt = rhsValue.intValue();
		int result;
		switch (expr.op) {
		case DIVIDE:
			if (rhsInt == 0)
				throw new RuntimeException("Attempt to divide by zero: " + expr);
			result = lhsInt / rhsInt;
			break;
		case MINUS:
			result = lhsInt - rhsInt;
			break;
		case MULTIPLY:
			result = lhsInt * rhsInt;
			break;
		case PLUS:
			result = lhsInt + rhsInt;
			break;
		case LT:
			result = lhsInt < rhsInt ? 1 : 0;
			break;
		case GT:
			result = lhsInt > rhsInt ? 1 : 0;
			break;
		case LTE:
			result = lhsInt <= rhsInt ? 1 : 0;
			break;
		case GTE:
			result = lhsInt >= rhsInt ? 1 : 0;
			break;
		case LAND:
			result = (lhsInt!=0 && rhsInt!=0) ? 1 : 0;
			break;
		case LOR:
			result = (lhsInt!=0 || rhsInt!=0) ? 1 : 0;
			break;
		default:
			throw new RuntimeException("Encountered unexpected operator type: " + expr.op);
		}
		return new Integer(result);
	}

	public Integer visit(Program program, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ClassList classes, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Class clss, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(DclrList list, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Field field, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ExtraIDs extraIDs, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Method method, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(FormalsList formalsList, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Formals formals, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Type type, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(CallStmt callStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ReturnStmt returnStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(IfStmt ifStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(WhileStmt whileStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(BreakStmt breakStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ContinueStmt continueStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(DeclarationStmt declarationStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(VirtualCall virtualCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(StaticCall staticCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ExprList expressions, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayVarExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayLenExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayAllocExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(StringExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(BooleanExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(NullExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}


}