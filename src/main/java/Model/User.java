package Model;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
public class User {

    private String username;

    private InetAddress ipaddress;

    private final List<User> contactList;

    public Boolean state;


    public User(){
        this.contactList = new ArrayList<>();
    }
    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public InetAddress getIPaddress(){
        return this.ipaddress;
    }

    public void setIPaddress(InetAddress ipaddress){
        this.ipaddress = ipaddress;
    }


    public List<User> getContactList(){
        return this.contactList;
    }

    public void addContact(User u){
        contactList.add(u);
    }

    public void deleteContact(User u){
        contactList.remove(u);
    }

    public Boolean containsContact(User u){
        return contactList.contains(u);
    }

    public Boolean getState(){
        return this.state;
    }

    public void setState(Boolean state){
        this.state = state;
    }


}
