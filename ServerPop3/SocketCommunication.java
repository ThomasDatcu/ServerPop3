package ServerPop3.ServerPop3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


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
            System.out.println("Creating Communication Socket");
            this.allUsers = allUsers;
            this.state = 0;
            this.s = s;
            this.inputFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.outputToClient = new DataOutputStream(s.getOutputStream());
            this.mailUser = null;

    }

    public void start(){
        System.out.println("Sending intialization message");
        this.writeBytes("+OK Serveur Pop3 Ready");
        this.flush();

        boolean exit = false;
        String clientUserName = "";
        String textFromClient = "";
        String[] splitTextFromClient = new String[5];

        while(!exit){

            try {
                    System.out.println("Awainting for customer request");
                    textFromClient = inputFromClient.readLine();
                    System.out.println("Client request : " + textFromClient);
                    splitTextFromClient = textFromClient.split(" ");
                    System.out.println("Data received from client, processing Data");
            } catch (IOException e) {
                    e.printStackTrace();
            }
            switch(state){

                case 0 :
                    System.out.println("Server is in AUTHORIZATION state");
                    //soit reception  APOP soit reception USER
                    if(splitTextFromClient[0].compareTo("APOP") == 0){
                        System.out.println("Server received an APOP command");
                        this.mailUser = allUsers.connect(splitTextFromClient[1], splitTextFromClient[2]);
                        if(this.mailUser != null){

                        /*if(splitTextFromClient[1].equals("tata") && splitTextFromClient[2].equals("toto")){						
                            this.writeBytes("+OK maildrop has 2 messages");
                            this.flush();*/
                            System.out.println("An user matching the username and the "
                                            + "password sent by the client has been found");
                            this.state = 2;
                            msgInMailDrop = this.mailUser.getNumberOfMessageInMaildrop();
                            mailDropLength = this.mailUser.getLengthOfMailDrop();
                            this.writeBytes("+OK " + mailUser.getName() + " maildrop has " + 
                                            msgInMailDrop + " messages (" + mailDropLength + "octects)");
                            this.flush();
                            System.out.println("Sending login confirmation to the user and moving to transaction state");
                        }else{
                            this.writeBytes("-ERR invalid username or password");
                            this.flush();
                            System.out.println("Sending err message to the client "
                                            + "because the input username/password is unknown");
                        }
                    }else if(splitTextFromClient[0].compareTo("USER") == 0){
                        System.out.println("Server received an USER command");
                        String userName = splitTextFromClient[1];
                        clientUserName = allUsers.chechUser(userName);
                        if(this.mailUser != null){
                            System.out.println("An user matching the input username has been found");
                            this.state = 1;
                            this.writeBytes("+OK waiting for " + userName + "'s password");
                            this.flush();
                            System.out.println("Moving to the awaiting password state ");
                        }else{
                            this.writeBytes("-ERR , sorry user : " + userName + "not found");
                            this.flush();
                            System.out.println("Sending err message to the client "
                                            + "because the input username is unknown");
                        }
                    }else if(splitTextFromClient[0].compareTo("QUIT") == 0){
                        this.writeBytes("+OK pop3 server is signing of");
                        this.flush();
                        exit = true;
                        System.out.println("Server is signing of");
                    }else{
                        this.writeBytes("-ERR unknown inbound action");
                        this.flush();
                        System.out.println("Unknown inboud action");
                    }
                    break;
                case 1 : 
                    System.out.println("Server is awaiting PASS command");
                    if(splitTextFromClient[0].compareTo("PASS")==0){
                        System.out.println("Server receive a PASS command");
                        this.mailUser = allUsers.connect(clientUserName, splitTextFromClient[1]);
                        if(this.mailUser != null){
                            System.out.println("Password is correct, connecting the user");
                            this.state = 2;
                            msgInMailDrop = this.mailUser.getNumberOfMessageInMaildrop();
                            mailDropLength = this.mailUser.getLengthOfMailDrop();
                            this.writeBytes("+OK " + mailUser.getName() + " has " + 
                                            msgInMailDrop + " messages (" + mailDropLength + "octects)");
                            this.flush();
                            System.out.println("Sending login confirmation to the user and moving to transaction state");
                        }else{
                            this.writeBytes("-ERR wrong password ");
                            this.flush();
                            this.state = 0;
                            System.out.println("Sen");
                        }
                    }else{
                        this.writeBytes("-ERR unknown inbound action");
                        this.flush();
                        System.out.println("Unknown inboud action");
                    }
                    break;
                case 2 : 
                    System.out.println("Server is in transaction state");
                    if(splitTextFromClient[0].compareTo("STAT") == 0){
                            System.out.println("Server Receive a STAT command");
                            this.writeBytes("+OK " + msgInMailDrop + " " + mailDropLength );
                            this.flush();
                            System.out.println("Sending number of messages and length of the messages in the maildrop");
                    }else if(splitTextFromClient[0].compareTo("LIST") == 0){
                        System.out.println("Server received a LIST command");
                        if(splitTextFromClient.length == 1){
                            System.out.println("There is no argument with the LIST command");
                            this.writeBytes("+OK " + msgInMailDrop + " message(s) ( " + mailDropLength + "octects)" );
                            this.flush();
                            for(int i = 0; i<msgInMailDrop;i++){
                                int msgLength = this.mailUser.getMessageLength(i);
                                this.writeBytes("+OK " + i + " " + msgLength);
                            }
                        }else{
                            int msgNumber = this.tryParse(splitTextFromClient[1]);
                            if(msgNumber != -1){
                                int msgLength = this.mailUser.getMessageLength(msgNumber);
                                switch(msgLength){
                                    case -1 : 
                                        this.writeBytes("-ERR this message is already marked as deleted");
                                        this.flush();
                                        break;
                                    case -2 :
                                        this.writeBytes("-ERR no such message");
                                        this.flush();
                                        break;
                                    default :
                                        this.writeBytes("+OK " + msgNumber + " " + msgLength );
                                        this.flush();
                                        break;
                                }																		
                            }else{
                                this.writeBytes("-ERR not a number");
                                this.flush();
                            }							
                        }						
                    }else if(splitTextFromClient[0].compareTo("RETR") == 0){
                        int msgNumber = this.tryParse(splitTextFromClient[1]);
                        if(msgNumber != -1){
                            String messageTxt = this.mailUser.getMessageText(msgNumber);
                            if(messageTxt.equals("-1")){
                                this.writeBytes("-ERR message marked as deleted");
                                this.flush();
                            }else if(messageTxt.equals("-2")){
                                this.writeBytes("-ERR no such message");
                                this.flush();
                            }else{
                                this.writeBytes("+OK sending message " + msgNumber);
                                this.writeBytes(messageTxt);
                                this.flush();
                            }
                        }else{
                            this.writeBytes("-ERR not a number");
                            this.flush();
                        }

                    }else if(splitTextFromClient[0].compareTo("DELE") == 0){
                        int msgNumber = this.tryParse(splitTextFromClient[1]);
                        if(msgNumber != -1){		
                            int errCode = this.mailUser.setMarkDeleted(msgNumber);
                            switch(errCode){
                                case 0 :
                                    this.writeBytes("+OK message marked as deleted");
                                    this.flush();
                                    break;
                                case -1 : 
                                    this.writeBytes("-ERR message " + msgNumber + " already deleted");
                                    this.flush();
                                    break;
                                case -2 : 
                                    this.writeBytes("-ERR no such message");
                                    this.flush();
                                    break;
                                default :
                                    System.out.println("This shouldn't happen");
                                    break;
                            }
                        }else{
                            this.writeBytes("-ERR not a number");
                            this.flush();
                        }
                    }else if(splitTextFromClient[0].compareTo("NOOP") == 0){
                        //TODO send an easter egg :-)
                    }else if(splitTextFromClient[0].compareTo("RSET") == 0){
                        this.mailUser.unmarkAllMessages();
                        this.writeBytes("+OK all messages unmark");
                        this.flush();
                    }else if(splitTextFromClient[0].compareTo("QUIT") == 0){
                        state = 3;
                    }else{
                        this.writeBytes("-ERR unknown inbound action");
                        this.flush();
                    }						
                    break;					
                case 3 :
                    this.mailUser.disconnect();
                    exit =true;
                    break;					
                default : 				
            }
        }

        this.close();


    }

    private void close() {
        // TODO Auto-generated method stub
        try {
            outputToClient.close();
            inputFromClient.close();
            s.close();
            //this.close(); -> WTF qu'est ce que tu as fais ici ?
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private int writeBytes(String s){
        try {
            s = s +"\r\n";
            System.out.println(s);
            outputToClient.write(s.getBytes());
            System.out.println(s.getBytes());
            //outputToClient.writeBytes(s);
                return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int flush(){
        try {
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
