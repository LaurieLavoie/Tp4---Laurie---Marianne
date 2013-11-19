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
          
            //2. get Input and Output streams
            out = new ObjectOutputStream(this.socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(this.socket.getInputStream());
            //3: Communicating with the server
            do{
                try{
                	
                	boolean quitter = false;
            		message = (String)in.readObject();

                	while(quitter == false)
                	{
                		
                		int messageClient = clientChoice();
                		System.out.println(messageClient);
                		System.out.println(quitter);
                		if (messageClient == 1)
                		{

                		}
                		else if(messageClient == 2)
                		{

                		}
                		else if(messageClient == 3)
                		{

                			quitter = true;
                    		System.out.println(quitter);
                        	System.out.println("server>" + message);
                        	sendMessage("Hi my server");		
//                       
                           	message = "bye";
                            sendMessage(message);

                		}	                	
//                    
                	}
                	
                }
                catch(ClassNotFoundException classNot){
                    System.err.println("data received in unknown format");
                }
            }while(!message.equals("bye"));
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
	private int clientChoice() 
	{
		int choice = 0;
    	
    	System.out.print("Entré 1 pour jouer, 2 pour voir vos scores ou 3 pour quitter: ");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line = in.readLine();
			System.out.println("Vous avez choisi :  " + choice);
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
				System.out.println("Client connecté sur le port :  " + line);

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
