package serveur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Random;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * La classe Serveur contient les fonctionnalités serveur du jeu "bonhomme pendu"
 * @author Marianne Giroux et Laurie Lavoie
 */

public class Serveur implements Runnable {
        
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
        	 try
        	 {
        		 this.out = new ObjectOutputStream(connection.getOutputStream());
		         this.out.flush();
		         in = new ObjectInputStream(connection.getInputStream());
		         boolean quit = false;
		        
		         //4. The two parts communicate via the input and output streams
		         do
		         {
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
                         
                         if(this.message.contains("quit"))
                         {
                        	 try
                             {
                                     clientMsg = xpath.evaluate("/quit", doc);
                                     this.quit();
                                	
                                     quit = true;
                             }
                        	 catch (XPathExpressionException e)
                             {
                        		 e.printStackTrace();
                             }
                         }

                         if(this.message.contains("sendWord"))
                         {
                        	 try
                        	 {
                                 clientMsg = xpath.evaluate("/sendWord", doc);
                                 this.sendWord();
                        	 }
                         
                        	 catch (XPathExpressionException e)
                        	 {    
                                 e.printStackTrace();
                        	 }
                         }
                         
                         if(this.message.contains("seeScore"))
                         {
                        	 
                        	 try
                        	 {
                                 clientMsg = xpath.evaluate("/seeScore", doc);
                                 this.getScore();
                        	 }
                        	 catch (XPathExpressionException e)
                        	 {
                                 
                                 e.printStackTrace();
                        	 }
                         }
                         
                         if(this.message.contains("newScore"))
                         {
                        	 try
                        	 {
                                 String nbTry = xpath.evaluate("/newScore/try", doc);
                                 String word = xpath.evaluate("/newScore/word", doc);
                                 this.setScore(word, nbTry);
                        	 }
                        	 catch (XPathExpressionException e)
                        	 {
                                 e.printStackTrace();
                        	 }
                         }
                         
                       
                         if(this.message.contains("name"))
                         {
	                         try
	                         {
                                 clientMsg = xpath.evaluate("/client/username", doc);
                                 clientPassword = xpath.evaluate("/client/password", doc);
                                 this.userExist(clientMsg, clientPassword);
	                         }
                         
	                         catch (XPathExpressionException e)
	                         {        
	                                 e.printStackTrace();
	                         }
                         
                         }
                         
                         if(this.message.contains("Name"))
                         {
	                         try
	                         {
	                                 clientMsg = xpath.evaluate("/client/newName", doc);
	                                 clientPassword = xpath.evaluate("/client/newPassword", doc);
	                                 this.createUser(clientMsg, clientPassword);
	                         }
	                         catch (XPathExpressionException e)
	                         {
	                        	 e.printStackTrace();
	                         }
                         }
                 }
      
		        catch(ClassNotFoundException classnot)
		        {
		        	System.err.println("Data received in unknown format");
		        }
       
		         }
		 while(!quit);
        	 }
         
        	 catch(IOException ioException)
        	 {
        		 ioException.printStackTrace();
        	 }
         
        	 finally
        	 {
        	 
         //4: Closing connection
        	 try
        	 {
        		 this.in.close();
        		 this.connection.close();
        		 this.out.close();
        		 this.ssock.close();
        	 }
        	 catch(IOException ioException)
        	 {
        		 ioException.printStackTrace();
        	 }
         }
     }
         /**
      	* Enregistre un score dans le fichier texte correspondant au nom d'utilisateur
      	* @param	clientMsg	Le nom d'utilisateur
      	* @param	typeScore	Le score de l'utilisateur
      	*/
         private synchronized void setScore(String word, String nbTry)
         {
             String xml = "";
             FileWriter writer = null;
             String text = "<score><try>" + nbTry + "</try><word>"  + word + "</word></score>\r\n";
             
             try
             {
            	 writer = new FileWriter(this.nameUser + ".txt", true);
                 writer.write(text,0,text.length());
                 xml = "<newScore>ok</newScore>";
                 sendMessage(xml);
             }
             catch(IOException ex)
             {
                 ex.printStackTrace();
                 xml = "<newScore>error</newScore>";
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
         
         /**
      	* Lit le port sur lequel le serveur doit se connecter, puis se connecte
      	*/
         private static int enteredPort()
         {
                 int port = 0;
                 
                 System.out.println("Entrez le port désiré: ");
                        try
                        {
                                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                                String line = in.readLine();
                                System.out.println("Serveur connecté sur le port : " + line);
                                port = Integer.parseInt(line);
                                
                        }
                        catch (IOException e)
                        {
                                e.printStackTrace();
                        }
                        
                        return port;
                 
         }
        
         /**
      	* Envoie à l'écran un message du serveur
      	* @param	msg		Le message à envoyer
      	*/
         private void sendMessage(String msg)
         {
	         try
	         {
	        	 out.writeObject(msg);
	        	 out.flush();
	        	 System.out.println("server>" + msg);
	         }
	         
	         catch(IOException ioException)
	         {
	        	 ioException.printStackTrace();
	         }
         }
         
         /**
      	* Permet de se connecter au serveur
      	*/
         public static void main(String args[])
         {
             int port = enteredPort();
            
             try
             {
                    ssock = new ServerSocket(port);
             }
             catch (IOException e)
             {
                    
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
                    e.printStackTrace();
                 }
                         
                 System.out.println("Connecté");
                 
                 Thread thread = new Thread(new Serveur(sock));   
                thread.start();
                try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             }
         }
        
         
//         public static String choose(File f) throws FileNotFoundException
//         {
//            String result = null;
//            Random rand = new Random();
//            int n = 0;
//            for(Scanner sc = new Scanner(f); sc.hasNext(); )
//            {
//               ++n; 
//               String line = sc.nextLine();
//               if(rand.nextInt(n) == 0)
//                  result = line;         
//            }
//
//            return result;      
//         }
//       }
         
         /**
      	* Envoie un mot au hasard à partir de la liste : "liste_français.txt"
      	*/
         private synchronized void sendWord() throws FileNotFoundException
         {
        	 
        	  	String word = "";
//             BufferedReader br = null;
//             try
//             {
//                     br = new BufferedReader(new FileReader("liste_francais.txt"));
//             }
//             catch (FileNotFoundException e)
//             {
//                    e.printStackTrace();
//             }
             
        	  	
        	  	int lines = 0;
        	  	BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader("liste_francais.txt"));
					
	        	  	while (reader.readLine() != null)
	        	  	{ 
	        	  		lines++;
	        	  	}
	        	  	
	        	  	
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	 
        	  	
       
	              
//	             try {
//					word = br.readLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	             
	             
	       
             
             
//             LineNumberReader reader = new LineNumberReader(br);
//             int nbLine = reader.getLineNumber();
        	  int randomWord = (int) (Math.random() * (lines));

        	  reader = new BufferedReader(new FileReader("liste_francais.txt"));
//             String word = "";
             for(int i = 0; i< randomWord ; i++)
             {
                 try
                 {
                	 word = reader.readLine();
                 }
                 catch (IOException e)
                 {
                	 e.printStackTrace();
                 }
             }
               
//        	  	
//        	   Random rand = new Random();
//              double n = 0;
//              for(Scanner sc = new Scanner("liste_francais.txt"); sc.hasNext(); )
//              {
//                 ++n; 
//                 String line = sc.nextLine();
//                 if(randomWord == n)
//                    word = line;         
//              }
////   	
             

//           String result = null;
//           Random rand = new Random();
//           int n = 0;
//           for(Scanner sc = new Scanner("liste_francais.txt"); sc.hasNext(); )
//           {
//              ++n; 
//              String line = sc.nextLine();
//              if(rand.nextInt(n) == randomWord)
//                 result = line;         
//           }
////        	  	
//        	 System.out.print("asdfdghjkliuytresA");
             String xml = "<word>" + word + "</word>";
             sendMessage(xml);
         }
        
         /**
      	* Permet de retrouver le meilleur score d'un utilisateur
      	*/
         private synchronized void getScore()
         {
        	 
        	 int nbScores = 0;
                 try
                 {    
                	
                    BufferedReader buff = new BufferedReader(new FileReader(this.nameUser + ".txt"));
                        
                    try
                    {
                    	String line;
                
                    	while ((line = buff.readLine()) != null)
                    	{
                            
                            if(line.contains("<score>"))
                            {
                            	nbScores++;
                                sendMessage(line);
                            }
                        
                    	}
                    	
                    	   if(nbScores == 0)
                           {
                           	 sendMessage("<score>Pas de score</score>");
                           }
                    } 
                    
                 
                    finally
                    {
                    	buff.close();
                    }
                        
                 }
                 catch (IOException ioe)
                 {
                	 
                	 sendMessage("<score>error</score>");
                 }
         }
        
         //Ferme le serveur
         private synchronized void quit()
         {
             System.out.println(this.nameUser + " se déconnecte. ");
             String xml = "<quit>ok</quit>";
             sendMessage(xml);
             
             try
        	 {
        		 this.in.close();
        		 this.connection.close();
        		 this.out.close();
        		 this.ssock.close();
        	 }
        	 catch(IOException ioException)
        	 {
        		 ioException.printStackTrace();
        	 }
         }
         
         /*Compare le mot de passe avec celui que l'utilisateur a entré et celui qui est contenu dans le fichier texte
         correspondant au nom d'utilisateur entré */
         
         private synchronized void comparePassword(String clientPassword, File file)
         {
        	   
             BufferedReader br = null;
             
             try
             {
            	 
            	 
                 br = new BufferedReader(new FileReader(file));
                                     
              }
              catch (FileNotFoundException e)
              {
                                    
                     e.printStackTrace();
              }
                     
             
             
//             
                
             String line = null;
                             
             try
             {
                                     
            	 line = br.readLine();
             } 
             catch (IOException e)
             {
                                  
            	 e.printStackTrace();
             }
                     
             XPathFactory xpathFactory = XPathFactory.newInstance();
             XPath xpath = xpathFactory.newXPath();
             InputSource source = new InputSource(new StringReader(line));
             Document doc = null;
                              
             try 
             {
            	 doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
             } 
             catch (XPathExpressionException e)
             {
            	 e.printStackTrace();
             }
                              
              
             
                             
             if(line.contains("/password"))
             {
                                      
            	 try 
            	 {
            		 clientPassword = xpath.evaluate("/client/password", doc);
            		 
            		 String xml = "<loginUser>ok</loginUser>";
            		 sendMessage(xml);
                             
            	 }
	            	 
            	 catch (XPathExpressionException e)
            	 {
	            		 String xml = "<client><password>error</password></client>";
                                                      
	            		 sendMessage(xml);
                             
	            		 e.printStackTrace();
            	 }
                                              
                                
                                      
                                              
             	}
                             
             	try
             	{
             		br.close();
             	}
             	catch (IOException e)
             	{
                                    
             		e.printStackTrace();
                 }
         }
         
        
         /**
      	* Permet de se connecter en tant qu'utilisateur en vérifiant le nom et mot de passe
      	* @param	clientMsg	Le nom d'utilisateur entré par l'utilisateur
      	* @param	Password	Le mot de passe entré par l'utilisateur
      	*/
         private synchronized void userExist(String clientMsg, String Password)
         {
        	 this.nameUser = clientMsg;
    
        	 File file = new File(clientMsg + ".txt");

        	 if (file.exists())
        	 {
        		 this.comparePassword(Password, file);
        	 }
        	 else
        	 {
        		 String xml = "<client><name>error</name></client>";
        		 sendMessage(xml);
        	 }
         }
        
     	/**
     	* Permet de créer un fichier texte contenant le nom de l'utilisateur et le mot de passe qu'il a choisi
     	* @param	clientMsg	Le nom d'utilisateur que l'utilisateur a choisi
     	* @param	Password	Le mot de passe que l'utilisateur a choisi
     	*/
         private synchronized void createUser(String clientMsg, String Password)
         {
                 this.nameUser = clientMsg;
                 File file = new File(clientMsg + ".txt");
                 String xml = "";
                 
                 if (!file.exists())
                 {
                     try
                     {
                         BufferedWriter writer = new BufferedWriter(new FileWriter(new File(clientMsg + ".txt")));
                         writer.write("<client><username>" + clientMsg + "</username>");
                         writer.write("<password>" + Password + "</password></client>\r\n");
                         writer.close();
                         xml = "<client><new>ok</new></client>";
                         sendMessage(xml);
                     }
                    
                     catch (IOException e)
                     {
                    	 e.printStackTrace();
                     }
                 }
                 
                 else
                 {
                     xml = "<client><new>error</new></client>";
                     sendMessage(xml);
                 }
         }
}