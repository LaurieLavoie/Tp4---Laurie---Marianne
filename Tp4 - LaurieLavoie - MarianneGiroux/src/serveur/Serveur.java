package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;




public class Serveur implements Runnable
{
	 	private static ServerSocket ssock;
	    private Socket connection = null;
	    private ObjectOutputStream out;
	    private ObjectInputStream in;
	    private String message;
	    private String nameUser = "";
	    public Serveur(Socket socket)
	    {
	    	this.connection = socket;
	    	this.run();
	    }
	    
	    public Serveur() 
	    {
	    	
		}

		public void run()
	    {
	        try{


	            this.out = new ObjectOutputStream(connection.getOutputStream());
	            this.out.flush();
	            in = new ObjectInputStream(connection.getInputStream());
	            boolean quitter = false;
	            //4. The two parts communicate via the input and output streams
	            do{
	            	
	            	
	                try
	                {
	                    message = (String)in.readObject();
	                    System.out.println("client>" + message);

	                    String clientMsg = "";
	                    XPathFactory xpathFactory = XPathFactory.newInstance();
	            		XPath xpath = xpathFactory.newXPath();

	            		InputSource source = new InputSource(new StringReader(message));
	            		Document doc = null;

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
	            			clientMsg = xpath.evaluate("/quitter", doc);
	            			this.quit();
	            			quitter = true;
	            		}
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/jouer", doc);
	            			this.sendWord();
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/voirScore", doc);
	            			this.getScore();
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/nouveauScore/essai", doc);
	            			this.setScore(clientMsg, "essai");
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/nouveauScore/mot", doc);
	            			this.setScore(clientMsg, "mot");
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/client/nom", doc);
	            			this.userExist(clientMsg);
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			
	            			e.printStackTrace();
	            		}
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/client/password", doc);
	            			this.comparePassword(clientMsg);
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		

	            	}
	       
	                
	                catch(ClassNotFoundException classnot)
	                {
	                    System.err.println("Data received in unknown format");
	                }
	            }while(!quitter);
	        }
	        catch(IOException ioException){
	            ioException.printStackTrace();
	        }
	        finally{
	            //4: Closing connection
	            try{
	                in.close();
	                out.close();
	                ssock.close();
	            }
	            catch(IOException ioException){
	                ioException.printStackTrace();
	            }
	        }
	    }
	   
	    
	 private void setScore(String clientMsg, String string) {
			// TODO Auto-generated method stub
			
		}

	public static int enteredPort()
	    {
	    	int port = 0;
	    	
	    	System.out.println("Entrez le port désiré: ");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = in.readLine();
				System.out.println("Serveur connecté sur le port :  " + line);

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
	            System.out.println("server>" + msg);
	        }
	        catch(IOException ioException){
	            ioException.printStackTrace();
	        }
	    }
	 public static void main(String args[]) throws Exception
	 {
		 int port = enteredPort();
		 
		 ssock = new ServerSocket(port);
		 System.out.println("En attente de connection");
		 while (true)
		 {
			 
			 Socket sock = ssock.accept();
			 System.out.println("Connecté");
			 new Thread(new Serveur(sock)).start();
	         

		}
	 }
	 
	 
	 public void sendWord()
	 {
		 
	 }
	 
	 public void getScore()
	 {
		 
	 }
	 
	 public void quit()
	 {
		 System.out.println(this.nameUser + " se déconnecte. :'( ");
	 }
	 
	 public void comparePassword(String clientMsg)
	 {
		 
	 }
	 public void userExist(String clientMsg)
	 {
		 this.nameUser = clientMsg;
	 }
	 
	 
	 
	 
}
