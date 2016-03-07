package ServerPop3;

public class Message {

	boolean isNewMessage;
	boolean toBeDeleted;
	String owner;
	String text;
	
	
	public Message(boolean isNewMessage,boolean toBeDeleted,String owner, String text){
		
		this.isNewMessage = isNewMessage;
		this.toBeDeleted = toBeDeleted;
		this.owner = owner;
		this.text = text;
		
	}
}
