package Model;
import java.net.InetAddress;
import java.util.List;
public class User {


    private String username;

    private InetAddress ipaddress;

    public Boolean state;

    // Contact List updated after connection, and each time another user connects
    private List<User> ContactList;

}
