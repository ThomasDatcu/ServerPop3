package ServerPop3;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserList {
	ArrayList<User> userList;
	
	public UserList(){
            //TO DO open file and load all users.
            FileInputStream fis;
            try{
                fis = new FileInputStream(new File("users.txt"));
                InputStreamReader lecteur = new InputStreamReader(fis);
                BufferedReader buff = new BufferedReader(lecteur);
                String ligne;
                while((ligne=buff.readLine())!=null){
                    String[] userIds = ligne.split(" ");
                    userList.add(new User(Integer.parseInt(userIds[0]), userIds[1], userIds[2]));
                }
                buff.close();
            }catch(FileNotFoundException e){
                System.out.println(e.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	
	public User connect(String name, String password){
            for(User mailUser : this.userList){
                if(mailUser.getName().compareTo(name) == 0){
                    if(mailUser.connect(password))
                        return mailUser;
                    else
                        return null;
                }
            }
            return null;
	}

	public String chechUser(String userName) {
            for(User mailUser : this.userList){
                if(mailUser.getName().compareTo(userName) == 0)
                    return userName;
            }
            return null;
	}
	
}
