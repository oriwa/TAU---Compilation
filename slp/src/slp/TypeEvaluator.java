package slp;

public class TypeEvaluator implements slp.PropagatingVisitor<Environment, TypeEntry>{

	@Override
	public TypeEntry visit(Program program, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ClassList classes, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(Class clss, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(DclrList list, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(Field field, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ExtraIDs extraIDs, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(Method method, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(FormalsList formalsList, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(Formals formals, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(Type type, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(StmtList stmts, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(AssignStmt stmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(CallStmt callStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ReturnStmt returnStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(IfStmt ifStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(WhileStmt whileStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(BreakStmt breakStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ContinueStmt continueStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(DeclarationStmt declarationStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(VirtualCall virtualCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(StaticCall staticCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ExprList expressions, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public TypeEntry visit(VarExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(ArrayVarExpr expr, Environment d) {
		
		return null;
	}

	@Override
	public TypeEntry visit(ArrayLenExpr expr, Environment d) {
		
		return d.getTypeEntry(Environment.INT);
	}

	@Override
	public TypeEntry visit(ArrayAllocExpr expr, Environment d) {
		TypeEntry arrayType= d.getTypeEntry(expr.type.name);
		TypeEntry arraySizeType = expr.expr.accept(this, d);
		d.validateTypeMismatch(d.getTypeEntry(Environment.INT), arraySizeType, expr.line);
		
		return ArrayTypeEntry.makeArrayTypeEntry(arrayType,expr.type.array_dimension);
	}

	@Override
	public TypeEntry visit(NumberExpr expr, Environment d) {
		return d.getTypeEntry(Environment.INT);
	}

	@Override
	public TypeEntry visit(StringExpr expr, Environment d) {
		return d.getTypeEntry(Environment.STRING);
	}

	@Override
	public TypeEntry visit(BooleanExpr expr, Environment d) {
		return d.getTypeEntry(Environment.BOOLEAN);
	}

	@Override
	public TypeEntry visit(NullExpr expr, Environment d) {
		return null;
	}

	@Override
	public TypeEntry visit(UnaryOpExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeEntry visit(BinaryOpExpr expr, Environment d) {
		return null;
	}

}
