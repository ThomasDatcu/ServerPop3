package ServerPop3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class SocketCommunication extends Thread {

	/**
	 * 0: Waiting for login (Authorization)
	 * 1: Waiting for password (Etat transitoire " attente de PASS ")
	 * 2: User logged (transaction)
	 * 3: Update
	 */
	int state;
	int idClient;
	String nameClient;
	Message MailBox[];
	int msgInMailDrop;
	int mailDropLength;
	Socket s;
	BufferedReader inputFromClient;
	DataOutputStream outputToClient;
	UserList allUsers;
	User mailUser;
	
	public SocketCommunication(Socket s, UserList allUsers) throws IOException{
		this.allUsers = allUsers;
		this.state = 0;
		this.s = s;
		this.inputFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.outputToClient = new DataOutputStream(s.getOutputStream());
		this.mailUser = null;
		
	}

	public void start(){
		try {
			outputToClient.writeBytes("+OK, Serveur Pop3 Ready");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	//	outputToClient.write(arg0, arg1, arg2);
		boolean exit = false;
		String clientUserName = "";
		String textFromClient = "";
		String[] splitTextFromClient = new String[5];
		
		while(!exit){
			
			try {
				textFromClient = inputFromClient.readLine();
				splitTextFromClient = textFromClient.split(" ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(state){
			
				case 0 :
					//soit reception  APOP soit reception USER
					if(splitTextFromClient[0].compareTo("APOP") == 0){
						this.mailUser = allUsers.connect(splitTextFromClient[1], splitTextFromClient[2]);
						if(this.mailUser != null){
							this.state = 2;
							msgInMailDrop = this.mailUser.getNumberOfMessageInMaildrop();
							mailDropLength = this.mailUser.getLengthOfMailDrop();
							//TODO send message successfuly log in
						}else{
							//TODO send message wrong username/password
						}
					}else if(splitTextFromClient[0].compareTo("USER") == 0){
						String userName = splitTextFromClient[1];
						clientUserName = allUsers.chechUser(userName);
						if(this.mailUser != null){
							this.state = 1;
							//TODO send message waiting for password
						}else{
							//TODO send message wrong username
						}
					}else if(splitTextFromClient[0].compareTo("QUIT") == 0){
						//TODO send server is signing off
					}else{
						//TODO send unknown inbound action
					}
					break;
				case 1 : 
					if(splitTextFromClient[0].compareTo("PASS")==0){
						this.mailUser = allUsers.connect(clientUserName, splitTextFromClient[1]);
						if(this.mailUser != null){
							this.state = 2;
							msgInMailDrop = this.mailUser.getNumberOfMessageInMaildrop();
							mailDropLength = this.mailUser.getLengthOfMailDrop();
							//TODO send message successfuly log in
						}else{
							//TODO send message wrong password
							//Que se passe t'il si on se trompe de password ? boucle sur l'état ou retour état 0 ?
						}
					}else{
						//TODO send unknown inbound action
					}
					break;
				case 2 : 
					// STAT / LIST[msg] / RETR msg / DELE msg / NOOP / RSET / QUIT
					if(splitTextFromClient[0].compareTo("STAT") == 0){
						//TODO send message with the number of message in the maildrop and the total length of the maildrop in octet
					}else if(splitTextFromClient[0].compareTo("LIST") == 0){
						if(splitTextFromClient.length == 1){
							//TODO send number of message and length of all the mailbox
							for(int i = 0; i<msgInMailDrop;i++){
								int msgLength = this.mailUser.getMessageLength(i);
								//TODO send message : +OK i msgLength
							}
						}else{
							int msgNumber = this.tryParse(splitTextFromClient[1]);
							if(msgNumber != -1){
								int msgLength = this.mailUser.getMessageLength(msgNumber);
								//TODO addCase messageIsMarkDeleted
								//TODO send message +OK i msgLength										
							}else{
								//TODO send message -ERREUR no such messsage
							}							
						}						
					}else if(splitTextFromClient[0].compareTo("RETR") == 0){
						
					}else if(splitTextFromClient[0].compareTo("DELE") == 0){
						
					}else if(splitTextFromClient[0].compareTo("NOOP") == 0){
						
					}else if(splitTextFromClient[0].compareTo("RSET") == 0){
						
					}else if(splitTextFromClient[0].compareTo("QUIT") == 0){
						
					}else{
						//TODO send unknown inbound action
					}
						
					break;					
				case 3 : 
					break;					
				default : 				
			}
		}
		
	
	}
	
	private int send(String s){
		
		try {
			outputToClient.writeBytes(s);
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	
	private int tryParse(String text) {
		  try {
		    return Integer.parseInt(text);
		  } catch (NumberFormatException e) {
		    return -1;
		  }
		}

}
