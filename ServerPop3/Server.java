package ServerPop3.ServerPop3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


	
public class Server {
	ServerSocket socket;
	UserList allUsers;
	
	public Server(){
		try {
			this.allUsers = new UserList();
			this.socket = new ServerSocket(110);
			System.out.println("Server Starting");
			
		} catch (IOException e) {
			System.out.println("Erreur impossible d'ouvrir le socket sur le port 110");
			e.printStackTrace();
		}
	}
	
	
	
	public void run(){
		
            boolean running = true;
            while(running){
                    Socket s = null;
                    try {
                            System.out.println("Server awaiting connection");
                            s = socket.accept();
                            this.initCommunication(s);
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
	}



	private void initCommunication(Socket s){
            try {
                SocketCommunication socketCom = new SocketCommunication(s, this.allUsers);
                socketCom.start();
                System.out.println("Socket communication start");
            } catch (IOException ex) {
                System.out.println("Connection closed");
            }
		
	}
	
}
