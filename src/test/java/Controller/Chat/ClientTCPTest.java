package Controller.Chat;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientTCPTest {

    @Test
    public void testClientTCP() throws UnknownHostException {
        // Créer une instance de la classe ClientTCP
        ClientTCP client = new ClientTCP();

        // Adresse IP et port pour la connexion (ajuster selon vos besoins)
        String serverIP = "localhost";
        int serverPort = 1556;

        // Démarrer la connexion
        client.startConnection(InetAddress.getByName(serverIP), serverPort);

        // Tester l'envoi de message
        String testMessage = "Hello, server!";
        client.sendMessage(testMessage);

        // Attendre un certain temps pour que le message soit traité par le serveur (ajuster selon les besoins)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Arrêter la connexion
        client.stopConnection();
    }
}
