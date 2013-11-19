package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;




public class Serveur
{
	 ServerSocket providerSocket;
	    Socket connection = null;
	    ObjectOutputStream out;
	    ObjectInputStream in;
	    String message;
	    Serveur(){}
	    void run()
	    {
	        try{

	        	int port = 0;
	        	port = this.enteredPort();
	            providerSocket = new ServerSocket(port, 10);

	            //2. Wait for connection
	            System.out.println("Attend une connection");
	            this.connection = this.providerSocket.accept();
	            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
	            //3. get Input and Output streams
	            this.out = new ObjectOutputStream(connection.getOutputStream());
	            this.out.flush();
	            in = new ObjectInputStream(connection.getInputStream());
	            sendMessage("Connection successful");
	            //4. The two parts communicate via the input and output streams
	            do{
	            	
	            	
	                try{
	                    message = (String)in.readObject();
	                    System.out.println("client>" + message);
	                    if (message.equals("bye"))
	                        sendMessage("bye");
	                }
	                catch(ClassNotFoundException classnot){
	                    System.err.println("Data received in unknown format");
	                }
	            }while(!message.equals("bye"));
	        }
	        catch(IOException ioException){
	            ioException.printStackTrace();
	        }
	        finally{
	            //4: Closing connection
	            try{
	                in.close();
	                out.close();
	                providerSocket.close();
	            }
	            catch(IOException ioException){
	                ioException.printStackTrace();
	            }
	        }
	    }
	   
	    
	    public int enteredPort()
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
	    public static void main(String args[])
	    {
	        Serveur server = new Serveur();
	        while(true){
	            server.run();
	        }
	    }
}
