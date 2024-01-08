package View;


import java.net.SocketException;
import java.net.UnknownHostException;

public interface CustomListener {
    void notUniquePopup(String message);
    void unique() throws SocketException, UnknownHostException, InterruptedException;
    void launchTest();
}
