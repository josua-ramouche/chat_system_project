package Controller.Chat;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerTCPTest {

    @Test
    public void testServerTCP() throws UnknownHostException {
        // Créer une instance de la classe ServerTCP
        ServerTCP server = new ServerTCP();

        // Démarrer le serveur dans un thread distinct
        Thread serverThread = new Thread(() -> server.main(null));
        serverThread.start();

        // Attendre un certain temps pour que le serveur puisse démarrer (ajuster selon les besoins)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Créer une instance de la classe ClientTCP pour simuler un client
        ClientTCP client = new ClientTCP();

        // Adresse IP et port pour la connexion (ajuster selon vos besoins)
        String serverIP = "localhost";
        int serverPort = 1556;

        // Démarrer la connexion du client
        client.startConnection(InetAddress.getByName(serverIP), serverPort);

        // Tester l'envoi de message depuis le client vers le serveur
        String testMessage = "Hello, server!";
        client.sendMessage(testMessage);

        // Attendre un certain temps pour que le message soit traité par le serveur (ajuster selon les besoins)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Arrêter la connexion du client
        client.stopConnection();

        // Arrêter le serveur
        serverThread.interrupt();
    }
}
