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

		boolean exit = false;
		String clientUserName = "";
		String textFromClient = "";
		String[] splitTextFromClient = new String[5];
		
		while(!exit){
			
			try {
				textFromClient = inputFromClient.readLine();
				splitTextFromClient = textFromClient.split(" ");
			} catch (IOException e) {
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
							this.send("+OK " + mailUser.getName() + " has " + 
									msgInMailDrop + " messages (" + mailDropLength + "octects)");
						}else{
							this.send("-ERR invalid username or password");
						}
					}else if(splitTextFromClient[0].compareTo("USER") == 0){
						String userName = splitTextFromClient[1];
						clientUserName = allUsers.chechUser(userName);
						if(this.mailUser != null){
							this.state = 1;
							this.send("+OK waiting for " + userName + "'s password");
						}else{
							this.send("-ERR , sorry user : " + userName + "not found");
						}
					}else if(splitTextFromClient[0].compareTo("QUIT") == 0){
						this.send("+OK pop3 server is signing of");
						this.close();
					}else{
						this.send("-ERR unknown inbound action");
					}
					break;
				case 1 : 
					if(splitTextFromClient[0].compareTo("PASS")==0){
						this.mailUser = allUsers.connect(clientUserName, splitTextFromClient[1]);
						if(this.mailUser != null){
							this.state = 2;
							msgInMailDrop = this.mailUser.getNumberOfMessageInMaildrop();
							mailDropLength = this.mailUser.getLengthOfMailDrop();
							this.send("+OK " + mailUser.getName() + " has " + 
									msgInMailDrop + " messages (" + mailDropLength + "octects)");
						}else{
							this.send("-ERR wrong password ");
							//Que se passe t'il si on se trompe de password ? boucle sur l'état ou retour état 0 ?
						}
					}else{
						this.send("-ERR unknown inbound action");
					}
					break;
				case 2 : 
					//RETR msg / DELE msg / NOOP / RSET / QUIT
					if(splitTextFromClient[0].compareTo("STAT") == 0){
						this.send("+OK " + msgInMailDrop + " " + mailDropLength );
					}else if(splitTextFromClient[0].compareTo("LIST") == 0){
						if(splitTextFromClient.length == 1){
							this.send("+OK " + msgInMailDrop + " message(s) ( " + mailDropLength + "octects)" );
							for(int i = 0; i<msgInMailDrop;i++){
								int msgLength = this.mailUser.getMessageLength(i);
								this.send("+OK " + i + " " + msgLength);
							}
						}else{
							int msgNumber = this.tryParse(splitTextFromClient[1]);
							if(msgNumber != -1){
								int msgLength = this.mailUser.getMessageLength(msgNumber);
								switch(msgLength){
									case -1 : 
										this.send("-ERR this message is already marked as deleted");
										break;
									case -2 :
										this.send("-ERR no such message");
										break;
									default :
										this.send("+OK " + msgNumber + " " + msgLength );
										break;
								}																		
							}else{
								this.send("-ERR not a number");
							}							
						}						
					}else if(splitTextFromClient[0].compareTo("RETR") == 0){
						int msgNumber = this.tryParse(splitTextFromClient[1]);
						if(msgNumber != -1){
							String messageTxt = this.mailUser.getMessageText(msgNumber);
							if(messageTxt.equals("-1")){
								this.send("-ERR message marked as deleted");
							}else if(messageTxt.equals("-2")){
								this.send("-ERR no such message");
							}else{
								this.send("+OK sending message " + msgNumber);
								this.send(messageTxt);
							}
						}else{
							this.send("-ERR not a number");
						}
						
					}else if(splitTextFromClient[0].compareTo("DELE") == 0){
						int msgNumber = this.tryParse(splitTextFromClient[1]);
						if(msgNumber != -1){		
							int errCode = this.mailUser.setMarkDeleted(msgNumber);
							switch(errCode){
								case 0 :
									this.send("+OK message marked as deleted");
									break;
								case -1 : 
									this.send("-ERR message " + msgNumber + " already deleted");
									break;
								case -2 : 
									this.send("-ERR no such message");
									break;
								default :
									System.out.println("This shouldn't happen");
									break;
							}
						}else{
							this.send("-ERR not a number");
						}
					}else if(splitTextFromClient[0].compareTo("NOOP") == 0){
						//TODO send an easter egg :-)
					}else if(splitTextFromClient[0].compareTo("RSET") == 0){
						this.mailUser.unmarkAllMessages();
						this.send("+OK all messages unmark");
					}else if(splitTextFromClient[0].compareTo("QUIT") == 0){
						state = 3;
					}else{
						this.send("-ERR unknown inbound action");
					}						
					break;					
				case 3 : 
					this.mailUser.disconnect();
					this.close();
					break;					
				default : 				
			}
		}
		
	
	}
	
	private void close() {
		// TODO Auto-generated method stub
		try {
			outputToClient.close();
			inputFromClient.close();
			s.close();
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private int send(String s){
		try {
			outputToClient.writeBytes(s);
			outputToClient.flush();
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
