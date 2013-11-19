package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client 
{

	private Client client;
	Socket socket;
	
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    
    
//    Requester(){
	
	
	public Client()
	{
		
	}
	
	void run()
    {
        try{
            //1. creating a socket to connect to the server
        	int port = 0;
        	port = this.enteredPort();
            this.socket = new Socket("localhost",port);
			System.out.println("Client connecté sur le port :  " + port);

            //2. get Input and Output streams
            out = new ObjectOutputStream(this.socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(this.socket.getInputStream());
        	boolean quitter = false;

            //3: Communicating with the server
            do{
                try{


                	while(quitter == false)
                	{

                		int messageClient = clientChoice();
                		
                		if (messageClient == 1)
                		{
                			this.playGame();
                		}
                		else if(messageClient == 2)
                		{
                			this.seeScore();
                		}
                		else if(messageClient == 3)
                		{

                			quitter = true;
                        	System.out.println("server>" + message);
                        	sendMessage("Hi my server");		
//                       
                           	message = "bye";
                            sendMessage(message);

                		}	                	
//                    
                	}
            		message = (String)in.readObject();

                }
                catch(ClassNotFoundException classNot){
                    System.err.println("data received in unknown format");
                }
            }while(!quitter);
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                this.socket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
	private void playGame() {
		// TODO Auto-generated method stub
		
	}

	private void seeScore() {
		// TODO Auto-generated method stub
		
	}


	private int clientChoice() 
	{
		int choice = 0;
    	
    	System.out.print("Entré 1 pour jouer, 2 pour voir vos scores ou 3 pour quitter: ");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line = in.readLine();
			System.out.println("Vous avez choisi :  " + line);
			choice = Integer.parseInt(line);
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return choice;
	}

	public int enteredPort()
	    {
	    	int port = 0;
	    	
	    	System.out.println("Entrez le port désiré: ");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = in.readLine();

				port =  Integer.parseInt(line);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return port;
	    	
	    }
    void sendMessage(String msg)
    {
        try{

            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    public static void main(String args[])
    {
        Client client = new Client();
    	client.run();
    }
	
	

}
