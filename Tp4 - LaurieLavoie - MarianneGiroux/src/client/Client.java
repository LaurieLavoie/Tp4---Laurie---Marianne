package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class Client 
{

	private Socket socket;
	
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String message;
    
    
	
	
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
        	String msgToClient = "";
        	do
        	{
        		try {
        			loginUser();
        			
					String msgXml = "";
					this.message = (String)in.readObject();
        			XPathFactory xpathFactory = XPathFactory.newInstance();
        			XPath xpath = xpathFactory.newXPath();
	            	InputSource source = new InputSource(new StringReader(this.message));
	            	Document doc = null;
	            	System.out.println("serveur>" + this.message);
	            	
	            	try
            		{
            			doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            		} 
            		catch (XPathExpressionException e)
            		{
            			
            			e.printStackTrace();
            		}
            		
            		try
            		{
            			try 
            			{
							msgXml = xpath.evaluate("/name/client", doc);
						} 
            			catch (XPathExpressionException e)
            			{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			try 
            			{
							msgXml = xpath.evaluate("/password/client", doc);
						} 
            			catch (XPathExpressionException e)
            			{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
	            	finally
	            	{
	            		
	            	}
	            	
            		
        			}
	            	catch (ClassNotFoundException e)
	            	{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		
        	}while(!msgToClient.equals("erreur"));
        	
        	
        	
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
		try 
		{
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
		System.out.println("Entrez votre nom d'utilisateur : ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String lineUsername = "";
		
		try {
			lineUsername = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Entrez votre mot de passe : ");
		BufferedReader inPsswd = new BufferedReader(new InputStreamReader(System.in));
		String linePassword = "";
		try {
			linePassword = inPsswd.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String xml = "<client><username>" + lineUsername + "</username><password>" + linePassword +"</password></client>";
		sendMessage(xml);
	}
	
	private void sendScore()
	{
		
	}
	private void createUser()
	{
		System.out.println("Entrez votre nom d'utilisateur : ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String lineUsername = "";
		
		try {
			lineUsername = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Entrez votre mot de passe : ");
		BufferedReader inPsswd = new BufferedReader(new InputStreamReader(System.in));
		String linePassword = "";
		try {
			linePassword = inPsswd.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String xml = "<client><newName>" + lineUsername + "</newName><newPassword>" + linePassword +"</newPassword></client>";
		sendMessage(xml);
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
