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



/**
 * La classe Client contient les fonctionnalités côté client du jeu "bonhomme pendu"
 * @author Marianne Giroux et Laurie Lavoie
 */


public class Client
{
        private Socket socket;
	    private ObjectOutputStream out;
	    private ObjectInputStream in;
	    private String message;
	    private char[] tabChar;
	    private boolean[] tabDiscoveredLetter;
        
	    
	    public Client()
	    {
	    	
	    }
	    
        /**
      	* Fait rouler l'application du côté client
      	*/
        public void run()
        {
        	try
        	{
            //1. creating a socket to connect to the server
                int port = 0;
                port = this.enteredPort();
                String iP = this.enterIp();
                this.socket = new Socket(iP,port);
                System.out.println("Client connecté sur le port : " + port + "à l'IP : " + iP);

	            //2. get Input and Output streams
	            this.out = new ObjectOutputStream(this.socket.getOutputStream());
	            this.out.flush();
	            this.in = new ObjectInputStream(this.socket.getInputStream());
	            boolean quitter = false;
	            String msgXml = "";
	            do
	            {
	            	try 
	            	{
	            		loginUser();
                                
						
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
                            	e.printStackTrace();
                            }
                                    
                            try
                            {
                            	msgXml = xpath.evaluate("/password/client", doc);
                            }
                            
                            catch (XPathExpressionException e)
                            {
                            	e.printStackTrace();
                            }
                            
                            
                            try
                            {
                            	msgXml = xpath.evaluate("/loginUser", doc);
                            }
                            
                            catch (XPathExpressionException e)
                            {
                            	e.printStackTrace();
                            }
                            
                        }
                        finally
                        {
                         
                        }   
                    }
	                
	            	catch (ClassNotFoundException e)
	                {
	                	e.printStackTrace();
	                }
                        
                }
	            while(!msgXml.equals("ok"));
                
                
                
            //3: Communicating with the server
            do
            {
                try
                {
                    while(quitter == false)
                    {
                        int messageClient = clientChoice();
                       
                		
                        if (messageClient == 1)
                        {
                        	
                        
                        	String xml = "<sendWord>send</sendWord>";
                        	this.sendMessage(xml);
                        	
                        	this.message = (String)in.readObject();
                        	 XPathFactory xpathFactory = XPathFactory.newInstance();
                        	 XPath xpath = xpathFactory.newXPath();
                			InputSource source = new InputSource(new StringReader(this.message));
                			Document doc = null;
                			System.out.println("serveur>" + this.message);
                    		String wordXml = "";
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
                            	wordXml = xpath.evaluate("/word", doc);
                            
                            }
                            
                            catch (XPathExpressionException e)
                            {
                            	e.printStackTrace();
                            }
                        	
                        	
                        	this.playGame(wordXml);
                        }
                        else if(messageClient == 2)
                        {
                        	this.seeScore();
                        }
                        
                        else if(messageClient == 3)
                        {
                        
                        	String xml = "<quitter>quitter</quitter>";
                        	sendMessage(xml);
                        	quitter = true;
                       }                 
                    }
                    
                    this.message = (String)this.in.readObject();
                }
                
                catch(ClassNotFoundException classNot)
                {
                    System.err.println("data received in unknown format");
                }
                
            }
            while(!quitter);
        }
        	
        catch(UnknownHostException unknownHost)
        {
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
        	
        finally
        {
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
    
        
        
    
    /**
  	* Permet de commencer une partie et de gérer les classes pour y jouer
  	*/
    private void playGame(String word)
    {
    	//TODO : recevoir le mot du serveur
    	
    	
		System.out.println(word);
		
    	
//    	int count = 0;
    	int length = word.length();
    	
    	//Tableau contenant chaque lettre du mot séparément
    	this.tabChar = new char[length];
    	
    	//Tableau contenant des booléens qui indiquent si la lettre du tableau de char à la même position a été
    	//découverte
    	this.tabDiscoveredLetter = new boolean[length];
    	 for (int i = 0; i < length; i++)
         {
         	this.tabDiscoveredLetter[i] = false;
     	}
    	boolean allDiscovered = false;
    	
    	while (allDiscovered == false)
    	{
    		//Vérifie si toutes les lettres du tableau ont été découvertes. Si oui, la partie est gagnée!!
    		for (int i = 0; i < length; i++)
    		{
    			if (this.tabDiscoveredLetter[i] == false)
    			{
    				break;
    			}
    			
    			if (i == length && this.tabDiscoveredLetter[i] == true)
    			{
    				allDiscovered = true;
    			}
    		}
    		
    		System.out.println("Veuillez entrer une lettre : ");
    		
    	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String enteredChar = "";
        	try 
        	{
				enteredChar = in.readLine();
				
			} 
        	catch (IOException e) 
        	{	
				e.printStackTrace();
			}
        	
        	this.enteredChar(enteredChar.charAt(0), length);
    	}
    	
    	System.out.println("Bravo, vous avez découvert le mot! ");
    }
    
    /**
  	* Gère le caractère entré pour déterminer s'il a déjà été entré ou s'il est contenu ou non dans le mot
  	*/
    private void enteredChar(char enteredChar, int length)
    {
    	for (int i = 0; i < length; i++)
    	{
    		if (this.tabDiscoveredLetter[i] == false)
    		{
    			if (this.tabChar[i] == enteredChar)
    			{
    				this.tabDiscoveredLetter[i] = true;
    			}
    		}	
    	}
    	System.out.println(this.tabChar[1]);
//    	this.displayWord(this.tabChar.toString());
    }
    
    /**
  	* Montre le nombre de lettres du mot par des "_" et les lettres déjà devinées sont affichées
  	*/
//    private void displayWord(String recievedWord)
//    {  	
//    	String displayedWord = "";
//    	
//    	for (int i = 0; i < recievedWord.length(); i++)
//    	{
//    		if (this.tabDiscoveredLetter[i] == true)
//    		{
//    			displayedWord += this.tabChar[i];
//    		}
//    		
//    		else
//    		{
//    			displayedWord += "_ ";
//    		}
//    	}
//    	System.out.print(displayedWord);
//    }
//        
    private void loginUser()
    {
            int choice = 0;
        System.out.print("Entrez 1 pour vous connecter, 2 pour créer un compte: ");
            try
            {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    String line = in.readLine();
                    System.out.println("Vous avez choisi : " + line);
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
            
            try
            {
            	lineUsername = in.readLine();
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
            
            System.out.println("Entrez votre mot de passe : ");
            BufferedReader inPsswd = new BufferedReader(new InputStreamReader(System.in));
            String linePassword = "";
            
            try
            {
            	linePassword = inPsswd.readLine();
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
            
            String xml = "<client><username>" + lineUsername + "</username><password>" + linePassword +"</password></client>";
            sendMessage(xml);
    }
    
    /**
  	* À la fin d'une partie, envoie le score au serveur afin qu'il l'enregistre
  	*/
//    private void sendScore()
//    {
//            
//    }
    
    
    private String enterIp()
    {
    	String ip = "";
    	
        do
        {
        	
        	
        		System.out.println("Entrez l'IP désiré: ");
	    	try
	    	{
	    		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    		ip = in.readLine();
	
	    		
	    	}
	    	
	    	catch (IOException e) 
	    	{
	    		e.printStackTrace();
	    	}
	    	
	    	
	    	if(ip.length() != 11)
	    	{
	    		System.out.println("Ip invalide, veuillez réessayer");
	    	}
        }while(ip.length() != 11);
    

    	          
    	
		return ip;
    	
    }
    
    /**
  	* Permet d'entrer un nouveau nom d'utilisateur ainsi qu'un nouveau mot de passe pour créer un compte sur 
  	* le serveur
  	*/
    private void createUser()
    {
            System.out.println("Entrez votre nom d'utilisateur : ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String lineUsername = "";
            
            try 
            {
            	lineUsername = in.readLine();
            } 
            catch (IOException e) 
            {
            	e.printStackTrace();
            }
            
            System.out.println("Entrez votre mot de passe : ");
            BufferedReader inPsswd = new BufferedReader(new InputStreamReader(System.in));
            String linePassword = "";
            try
            {
            	linePassword = inPsswd.readLine();
            }
            catch (IOException e) 
            {
            	e.printStackTrace();
            }
            
            String xml = "<client><newName>" + lineUsername + "</newName><newPassword>" + linePassword +"</newPassword></client>";
            sendMessage(xml);
    }
    
    /**
  	* Montre le meilleur score enregistré d'un utilisateur
  	*/
    private void seeScore()
    {
    	
    }
     
    /**
  	* Quitte l'application côté client
  	*/
//    private void quit()
//    {
//    	try 
//    	{
//			this.socket.close();
//			System.out.println("Fermeture de la session");
//		} catch (IOException e) {
//			System.out.println("La session n'a pas pu être fermée");
//		}
//    }
        
    /**
	* Génère la XML à envoyer au serveur
	*/
//	private void generateXml()
//	{
//		
//	}




	/**
  	* Gère le menu
  	*/
    private int clientChoice()
    {
    	int choice = 0;
        System.out.print("Entrez 1 pour jouer, 2 pour voir vos scores ou 3 pour quitter: ");
        
        try 
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = in.readLine();
            System.out.println("Vous avez choisi : " + line);
            choice = Integer.parseInt(line);
        }
        
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
            
        return choice;
    }

    private int enteredPort()
    {
    	int port = 0;
             
    	System.out.println("Entrez le port désiré: ");
    	try
    	{
    		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    		String line = in.readLine();

    		port = Integer.parseInt(line);
    	}
    	
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}

    	return port;            
    }
      
    /**
  	* Envoie un message au serveur
  	*/
    void sendMessage(String msg)
    {
        	try
        	
        	{
        		this.out.writeObject(msg);
        		this.out.flush();
        		System.out.println("client>" + msg);
        	}
        	
        	catch(IOException ioException)
        	{
        		ioException.printStackTrace();
        	}
    }
        
    public static void main(String args[])
    {
    	
    	Client client = new Client();
//    	client.run();
    }
}