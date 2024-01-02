package View;


import java.net.SocketException;

public interface CustomListener {
    void notUniquePopup(String message);
    void unique() throws SocketException;
    void launchtest();
}
