package ServerPop3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private int id;
    private String name;
    private String password;
    private ArrayList<Message> mails;
    private int nbMessages;

    public User(int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
        //TO DO handle mails arraylist
        FileInputStream fis;
        try{
            fis = new FileInputStream(new File("mails/user_"+ id +".txt"));
            InputStreamReader lecteur = new InputStreamReader(fis);
            BufferedReader buff = new BufferedReader(lecteur);
            String ligne;
            while((ligne=buff.readLine())!=null){
                String[] messageId = ligne.split(" ");
                mails.add(new Message(Integer.parseInt(messageId[0]), Boolean.getBoolean(messageId[1]), buff.readLine()));
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        nbMessages = mails.size();
    }

    String getName() {
        return this.name;
    }

    public boolean connect(String password) {
        return this.password.compareTo(password) == 0;
    }

    public int getNumberOfMessageInMaildrop() {
        return this.nbMessages;
    }

    public Message getMessage(int id){
        for(Message mail : this.mails){
            if(mail.getId() == id){
                return mail;
            }
        }
        return null;
    }
    
    public int getLengthOfMailDrop() {
        int res = 0;
        for(Message mail : this.mails){
            res += mail.size;
        }
        return res;
    }
    /**
     * 
     * @param i
     * @return length of message or 
     * -1: message marked as deleted
     * -2: message not found
     */
    public int getMessageLength(int i) {
        Message mail = getMessage(i);
        if(mail == null)
            return -2;
        if(mail.isToDelete())
            return -1;
        return mail.getSize();
    }
    
    public String getMessageText(int i){
        Message mail = getMessage(i);
                if(mail == null)
            return "-2";// : mail not found !
        if(mail.isToDelete())
            return "-1";// : mail will be destroyed !
        mail.isNewMessage = false;
        return mail.getText();
    }

    /**
     * 
     * @param i
     * @return 0 if worked well
     * -1 if message marked as delete
     * -2 if message not found
     */
    public int setMarkDeleted(int i) {
        Message mail = getMessage(i);
        if(mail == null)
            return -2;
        if(mail.isToDelete())
            return -1;
        return mail.setToDelete();
    }
    
    public void unmarkAllMessages(){
        for(Message mail : this.mails){
            mail.setNotToDelete();
        }
    }
    
    public int disconnect(){
        FileOutputStream fos;
        id = 0;
        try{
            fos = new FileOutputStream(new File("mails/user_"+ id +".txt"));
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            BufferedWriter buff = new BufferedWriter(writer);
            for(Message mail : this.mails){
                if(!mail.toBeDeleted){
                    buff.write("" + id + mail.isNewMessage + "/n");
                    buff.write(mail.text);
                }
                id++;
            }
            buff.close();
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
	
}
