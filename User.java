package ServerPop3;

import java.util.ArrayList;

public class User {
	private String name;
	private String password;
	private ArrayList<Message> mails;
	
	public User(String name, String password){
		this.name = name;
		this.password = password;
		//TO DO handle mails arraylist
	}
	
}
