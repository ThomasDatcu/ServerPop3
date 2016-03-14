package ServerPop3.ServerPop3;

public class Message {

    int idMessage;
    String text;
    int size;
    boolean isNewMessage;
    boolean toBeDeleted;

    public Message(int idMessage, boolean isNewMessage, String text){
        this.idMessage = idMessage;
        this.isNewMessage = isNewMessage;
        this.toBeDeleted = false;
        this.text = text;
        this.size = text.length();
    }
    
    public int setToDelete(){
        this.toBeDeleted = true;
        return 0;
    }
    
    public void read(){
        this.isNewMessage = false;
    }
    
    public int getSize(){
        return size;
    }
    
    public String getText(){
        return text;
    }

    public int getId() {
        return this.idMessage;
    }

    boolean isToDelete() {
        return this.toBeDeleted;
    }

    void setNotToDelete() {
        this.toBeDeleted = false;
    }
}
