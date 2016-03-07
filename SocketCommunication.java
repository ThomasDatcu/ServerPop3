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
	
	public SocketCommunication(Socket s) throws IOException{
		
		this.state = 0;
		this.s = s;
		this.inputFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.outputToClient = new DataOutputStream(s.getOutputStream());
		
		
	}

	public void start(){
		Byte[] output;
	//	outputToClient.writeByte("");
	//	outputToClient.write(arg0, arg1, arg2);
		boolean exit = false;
		while(!exit){
			try {
				inputFromClient.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(state){
			case 0 :
				
			}
		}
		
	
	}

}
