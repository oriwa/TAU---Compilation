package slp;

public class ArrayTypeEntry extends TypeEntry {
	private int dimension;
	
	private ArrayTypeEntry(int entryId,String entryName){
		super(entryId,entryName);
	}
	
	private ArrayTypeEntry(int entryId,String entryName, Class entryClass){
		super(entryId,entryName, entryClass);
	}
	
	public static ArrayTypeEntry makeArrayTypeEntry(TypeEntry arrayType, int arrayDimension) {
		ArrayTypeEntry result=null;
		if(arrayType.isPrimitive()){
			result=new ArrayTypeEntry(arrayType.getEntryId(),arrayType.getEntryName());
		}
		else{
			result=new ArrayTypeEntry(arrayType.getEntryId(),arrayType.getEntryName(),arrayType.getEntryClass());
		}
		result.setTypeDimension(arrayDimension);
		return result;
		
	}
	private void setTypeDimension(int newDimension){
		this.dimension=newDimension;
	}
	
	public int getTypeDimension(){
		return dimension;
	}
	
	public String getEntryName() {
		String arrBrackets =new String(new char[dimension]).replace("\0","[]"); 
		return super.getEntryName()+arrBrackets;
	}

}
