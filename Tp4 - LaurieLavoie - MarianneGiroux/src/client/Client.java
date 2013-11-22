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

	private Socket socket;
	
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String message;
    
    
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
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(this.socket.getInputStream());
        	boolean quitter = false;
        	loginUser();
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
                        	System.out.println("server>" + this.message);
                        	sendMessage("Hi my server");		
//                       
                           	this.message = "bye";
                            sendMessage(this.message);

                		}	                	
//                    
                	}
            		this.message = (String)this.in.readObject();

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
                this.in.close();
                this.out.close();
                this.socket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
	private void playGame() 
	{
		
	}
	private void enteredChar()
	{
		
	}
	private void displayWord()
	{
		
	}
	private void loginUser()
	{
		int choice = 0;
    	
    	System.out.print("Entré 1 pour vous connecter, 2 pour créer un compte: ");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line = in.readLine();
			System.out.println("Vous avez choisi :  " + line);
			choice = Integer.parseInt(line);
		
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(choice == 1)
		{
			userExist();
		}
		else if(choice == 2)
		{
			createUser();
		}
		
		
		
	}
	
	private void userExist()
	{
		
	}
	
	private void sendScore()
	{
		
	}
	private void createUser()
	{
		
	}
	
 	private void seeScore()
	{
		
	}
	private void generateXml()
	{
		
	}
	private void quit()
	{
		
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

            this.out.writeObject(msg);
            this.out.flush();
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
