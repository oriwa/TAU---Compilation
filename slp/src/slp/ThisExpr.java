package slp;

public class ThisExpr extends Expr {


	public final int line;
	
	public ThisExpr(int line)
	{
		this.line=line;
	}
	
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {

		return visitor.visit(this, context);
	}
	public String toString() {
		return "this object";
	}	

}
