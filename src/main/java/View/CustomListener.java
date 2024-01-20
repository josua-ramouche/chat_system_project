package View;


import java.net.SocketException;
import java.net.UnknownHostException;

//Publish : ServerUDP and ClientUDP   |  Subscribes : LoginApp and ChangeUsernameApp
//Uses : Check the unicity of usernames and refresh the contact list
public interface CustomListener {
    void notUniquePopup(String message);
    void unique() throws SocketException, UnknownHostException, InterruptedException;
    void launchTest();
}
