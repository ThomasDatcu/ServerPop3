package ServerPop3;

import java.util.ArrayList;

public class UserList {
	ArrayList<User> userList;
	
	public UserList(){
		//TO DO open file and load all users.
	}
	
	public User connect(String name, String password){
		return new User("test", "test");
	}

	public String chechUser(String userName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
