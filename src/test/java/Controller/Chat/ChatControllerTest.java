package Controller.Chat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

public class ChatControllerTest {

    @Test
    public void testChatController() {
        // Créer une instance de la classe ChatController
        ChatController chatController = new ChatController();

        // Créer une instance de la classe listenTCP
        ChatController.listenTCP listenThread = new ChatController.listenTCP();

        // Lancer le thread d'écoute
        listenThread.start();

        // Attendre un certain temps pour que le serveur puisse démarrer (ajuster selon les besoins)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Tester la connexion en créant un client (assurez-vous que le port est correct)
        boolean clientConnected = simulateClientConnection("localhost", 1556);

        // Arrêter le thread d'écoute
        listenThread.interrupt();

        // Vérifier que la connexion a réussi
        assertTrue(clientConnected);
    }

    // Méthode pour simuler la connexion d'un client
    private boolean simulateClientConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("CLIENT: Connected to server on port " + port);
            return true;
        } catch (IOException e) {
            System.out.println("CLIENT: Failed to connect to server");
            return false;
        }
    }
}
