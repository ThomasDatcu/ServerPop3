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
							//TODO send message successfuly log in
						}else{
							//TODO send message wrong password
							//Que se passe t'il si on se trompe de password ? boucle sur l'�tat ou retour �tat 0 ?
						}
					}else{
						//TODO send unknown inbound action
					}
					break;
				case 2 : 
					// STAT / LIST[msg] / RETR msg / DELE msg / NOOP / RSET / QUIT
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

}
