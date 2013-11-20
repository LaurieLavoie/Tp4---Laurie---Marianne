package serveur;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
	                    String clientPassword = "";
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
	            			clientPassword = xpath.evaluate("/client/password", doc);
	            			this.userExist(clientMsg, clientPassword);
	            		} 
	            		catch (XPathExpressionException e) 
	            		{
	            			
	            			e.printStackTrace();
	            		}
	            		
	            		try 
	            		{
	            			clientMsg = xpath.evaluate("/client/newPassword", doc);
	            			clientPassword = xpath.evaluate("/client/nom", doc);
	            			this.createUser(clientMsg, clientPassword);
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

	 private synchronized void  setScore(String clientMsg, String typeScore)
	 {
		 String xml = "";
		 FileWriter writer = null;
		 String text = "<score><" + typeScore + ">" + typeScore + ": " + clientMsg + "</" + typeScore + "></score>";
		 try
		 {
		      writer = new FileWriter(this.nameUser + ".txt", true);
		      writer.write(text,0,text.length());
		      xml = "<newScore>"+ typeScore +"ok</" + typeScore +"</newScore>";
		      sendMessage(xml);
		 }
		 catch(IOException ex)
		 {
		     ex.printStackTrace();
		     xml = "<newScore>erreur</newScore>";
		     sendMessage(xml);
		 }
		 
		 finally
		 {
		   if(writer != null)
		   {
		      try
		      {
				writer.close();
		      } 
		      catch (IOException e) 
		      {
				e.printStackTrace();
		      }
		   
		   }
		 }
			
	 }
	 private static int enteredPort()
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
	    
	 private void sendMessage(String msg)
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
	 public static void main(String args[]) 
	 {
		 int port = enteredPort();
		 
		 try 
		 {
			ssock = new ServerSocket(port);
		 } catch (IOException e)
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 System.out.println("En attente de connection");
		 while (true)
		 {
			 
			 Socket sock = null;
			 try 
			 {
				sock = ssock.accept();
			 }
			 catch (IOException e)
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 System.out.println("Connecté");
			 new Thread(new Serveur(sock)).start();
		}
	 }
	 
	 private synchronized void sendWord() 
	 {
		 BufferedReader br = null;
		 try 
		 {
			 br = new BufferedReader(new FileReader("listeMots.txt"));
		 }
		 catch (FileNotFoundException e) 
		 {
			e.printStackTrace();
		 }

		 LineNumberReader reader = new LineNumberReader(br);
		 int nbLine = reader.getLineNumber();
		 double randomWord = Math.random() * (nbLine);
		 String word = "";
		 for(int i = 0; i< randomWord ; i++)
		 {
			 try 
			 {
				word = br.readLine();
			 }
			 catch (IOException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		 }
		 
		 String xml = "<word>" + word + "</word>";
		 sendMessage(xml);
	
	 }
	 
	 private synchronized void getScore()
	 {
		 try
		 {
			
			BufferedReader buff = new BufferedReader(new FileReader(this.nameUser + ".txt"));
			 
			try
			{
			String line;
		
			while ((line = buff.readLine()) != null) 
			{
				System.out.println(line);
				if(line.contains("<score>"))
				{
					sendMessage(line);
				}
			
			}
			
			} finally 
			{
				buff.close();
			}
			
			} 
		 catch (IOException ioe) 
		 {
			System.out.println("Erreur --" + ioe.toString());
			sendMessage("<score>Erreur</score>");
		}
		 
		 
	 }
	 
	 private synchronized void quit()
	 {
		 System.out.println(this.nameUser + " se déconnecte.  ");
		 String xml = "<quitter>ok</quitter>";
		 sendMessage(xml);
	 }
	 
	 private synchronized void comparePassword(String clientPassword, File file) 
	 {
		 
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		 
 		boolean find = false;
		String passwordLine = "";
		BufferedReader br = null;
		
			try 
			{
				br = new BufferedReader(new FileReader(file));
			}
			catch (FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = null;
				
				line = br.readLine();
			
		        
		        while (!find) 
		        {
		            sb.append(line);
		            sb.append('\n');
		            line = br.readLine();
		            if(line.contains("password"))
		            {
		            	find = true;
		            	passwordLine  =  xpath.evaluate("/password", doc); 
		            	
		            }
		        }
		        
		    } catch (IOException | XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    finally 
		    {
		        try 
		        {
					br.close();
				} 
		        catch (IOException e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		 
		 
		 if(clientPassword.equals(passwordLine))
		 {
			 String xml = "<client><passWord>ok</passWord></client>";
			 sendMessage(xml);
		 }
		 else
		 {
			 String xml = "<client><passWord>erreur</passWord></client>";
			 sendMessage(xml); 
		 }
		 
	 }
	 
	 private synchronized void userExist(String clientMsg, String Password) 
	 {
		this.nameUser = clientMsg;
		
		File file = new File(clientMsg + ".txt");
		
		if (file.isFile())
		{
			this.comparePassword(Password, file);
			
		}
		else
		{
			String xml = "<client><name>Erreur</name></client>";
			sendMessage(xml);
		}
	 }
	 
	 private synchronized void createUser(String clientMsg, String Password)
	 {
		 this.nameUser = clientMsg;
		 
		 File file = new File(clientMsg + ".txt");
		 String xml = "";
		 if (!file.exists())
		 {
			 try{
				 BufferedWriter writer = new BufferedWriter(new FileWriter(new File(clientMsg + ".txt")));
				 
				 writer.write("<username>" + clientMsg + "</username>");
				 writer.write("<password>" + Password + "</password>");
				 writer.close();
				 xml = "<client><new>ok</new></Client>";
				 sendMessage(xml);
				 
			 }
			 
			 catch (IOException e)
			 {
				 e.printStackTrace();
			 }
		 }
		 else
		 {
			 xml = "<client><new>erreur</new></Client>";
			 sendMessage(xml);
		 }
	 }
}