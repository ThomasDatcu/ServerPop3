package ServerPop3;

public class Message {

	int idMessage;
	boolean isNewMessage;
	boolean toBeDeleted;
	String owner;
	String text;
	
	
	public Message(boolean isNewMessage,boolean toBeDeleted,String owner, String text, int idMesage){
		
		this.idMessage = idMessage;
		this.isNewMessage = isNewMessage;
		this.toBeDeleted = toBeDeleted;
		this.owner = owner;
		this.text = text;
		
	}
}
