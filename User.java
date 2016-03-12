package ServerPop3;


import java.util.ArrayList;

public class User {
    private int id;
    private String name;
    private String password;
    private ArrayList<Message> mails;

    public User(int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
        //TO DO handle mails arraylist
    }

    String getName() {
        return this.name;
    }

    public boolean connect(String password) {
        return this.password.compareTo(password) == 0;
    }

	public int getNumberOfMessageInMaildrop() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLengthOfMailDrop() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMessageLength(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMarkDeleted(int i) {
		// TODO Auto-generated method stub
		
	}
	
}
