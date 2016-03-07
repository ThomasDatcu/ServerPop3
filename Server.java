package ServerPop3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


	
public class Server {
	ServerSocket socket;
	
	public Server(){
		try {
			
			this.socket = new ServerSocket(110);
			
		} catch (IOException e) {
			System.out.println("Erreur impossible d'ouvrir le socket sur le port 110");
			e.printStackTrace();
		}
	}
	
	
	
	public void run() throws IOException{
		
		boolean running = true;
		while(running){
			Socket s = null;
			try {
				s = socket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.initCommunication(s);
		}
	}



	private void initCommunication(Socket s) throws IOException {
		// TODO Auto-generated method stub
		SocketCommunication socketCom = new SocketCommunication(s); 
		socketCom.start();
		
	}
	
}
