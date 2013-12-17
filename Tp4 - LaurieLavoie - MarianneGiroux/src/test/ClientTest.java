package test;
import org.junit.Test;
import org.junit.Assert;

import client.Client;


//Test de la classe client
//La plupart des méthodes sont privées, alors il n'est pas possible de les tester.
public class ClientTest
{
	//Test du constructeur
	@Test
	public void testConstructor() 
	{
		Client client = new Client();
		Assert.assertNotNull(client);
	}

	

}
