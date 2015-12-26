package slp;

public class IRVisitResult {

	public TypeEntry type;
	public Object value;
	public String moveInstruction="Move";
	
	public IRVisitResult()
	{
		
	}
	
	public IRVisitResult(TypeEntry type,Object value)
	{
		this.type=type;
		this.value=value;
	}
	
}
