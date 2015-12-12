package slp;

public class TypeEvaluator implements slp.PropagatingVisitor<Environment, Integer>{
	
	
	@Override
	public Integer visit(Program program, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ClassList classes, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Class clss, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(DclrList list, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Field field, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ExtraIDs extraIDs, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Method method, Environment d) {
		// TODO Auto-generated method stub
		//	TODO set d.currentMethod to this method's MethodSymbolEntry
		//	TODO add all formals as TypeEntries to its args-list using .addToArgs
		return null;
	}

	@Override
	public Integer visit(FormalsList formalsList, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Formals formals, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Type type, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(StmtList stmts, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(AssignStmt stmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(CallStmt callStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ReturnStmt returnStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(IfStmt ifStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(WhileStmt whileStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(BreakStmt breakStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ContinueStmt continueStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(DeclarationStmt declarationStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(VirtualCall virtualCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(StaticCall staticCall, Environment d) {
		//staticCall.className
		//staticCall.callArgs
		//staticCall.name
		//staticCall.line
		return null;
	}

	@Override
	public Integer visit(ExprList expressions, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(VarExpr expr, Environment d) {
		//TODO
		return null;
		/*
		 * if(expr.target_expr==null){
			TypeEntry exprType=expr.target_expr.accept(this, d);
			
			return d.getExprType(exprType,expr.name);
		}
		
		return d.getExprType(expr.name);*/
	}

	public Integer visit(ThisExpr expr, Environment d) {
		return d.getCurrentClass().getEntryId();
	}
	
	@Override
	public Integer visit(ArrayVarExpr expr, Environment d) {
		TypeEntry exprType=d.getTypeEntry(expr.target_expr.accept(this, d));
		Validator.validateIsArray(exprType, expr.line);
		
		TypeEntry idxType=d.getTypeEntry(expr.index_expr.accept(this, d));
		Validator.validateTypeMismatch(d.getTypeEntry(Environment.INT), idxType, expr.line,d);
		
		return ArrayTypeEntry.makeArrayTypeEntry(exprType,exprType.getTypeDimension()-1).getEntryId();
	}

	@Override
	public Integer visit(ArrayLenExpr expr, Environment d) {
		TypeEntry exprType = d.getTypeEntry(expr.expr.accept(this, d));
		Validator.validateIsArray(exprType,expr.line);
		return d.getTypeEntry(Environment.INT).getEntryId();
	}

	@Override
	public Integer visit(ArrayAllocExpr expr, Environment d) {
		TypeEntry arrayType= d.getTypeEntry(expr.type.name);
		TypeEntry arraySizeType = d.getTypeEntry(expr.expr.accept(this, d));
		Validator.validateTypeMismatch(d.getTypeEntry(Environment.INT), arraySizeType, expr.line,d);
		
		return ArrayTypeEntry.makeArrayTypeEntry(arrayType,expr.type.array_dimension).getEntryId();
	}

	@Override
	public Integer visit(NumberExpr expr, Environment d) {
		return d.getTypeEntry(Environment.INT).getEntryId();
	}

	@Override
	public Integer visit(StringExpr expr, Environment d) {
		return d.getTypeEntry(Environment.STRING).getEntryId();
	}

	@Override
	public Integer visit(BooleanExpr expr, Environment d) {
		return d.getTypeEntry(Environment.BOOLEAN).getEntryId();
	}

	@Override
	public Integer visit(NullExpr expr, Environment d) {
		return null;
	}

	@Override
	public Integer visit(UnaryOpExpr expr, Environment d) {
		int line= expr.line;
		TypeEntry exprType=d.getTypeEntry(expr.accept(this, d));   
		Validator.validateIlegalOp(exprType, expr.op, line,d);
		return exprType.getEntryId();
	}

	@Override
	public Integer visit(BinaryOpExpr expr, Environment d) {
		int line =expr.line;
				
		TypeEntry lType=d.getTypeEntry(expr.lhs.accept(this, d));
		TypeEntry rType=d.getTypeEntry(expr.rhs.accept(this, d));

		Validator.validateIlegalOp(lType,rType,expr.op, line,d);
		
		switch (expr.op){
		case PLUS:
			if(lType.getEntryId()==d.getTypeEntry(Environment.STRING).getEntryId()){
				return d.getTypeEntry(Environment.STRING).getEntryId();
			}
		case MINUS:
		case MULTIPLY:
		case DIVIDE:
		case MOD:
			return d.getTypeEntry(Environment.INT).getEntryId();
		default:
			//All other operations yield boolean result
			return d.getTypeEntry(Environment.BOOLEAN).getEntryId();
		}
		
	}

}
