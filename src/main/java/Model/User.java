package Model;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private InetAddress ipaddress;
    private List<User> contactList;
    public Boolean state;

    //constructors
    public User(){
        this.contactList = new ArrayList<>();
    }

    public User(String username, InetAddress ipaddress){
        this.contactList = new ArrayList<>();
        this.username = username;
        this.ipaddress = ipaddress;
    }

    public User(String username, InetAddress ipaddress, Boolean state){
        this.contactList = new ArrayList<>();
        this.username = username;
        this.ipaddress = ipaddress;
        this.state = state;
    }
    //Getters and Setters for username, IPAddress, contact list and status
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public InetAddress getIPAddress(){
        return this.ipaddress;
    }
    public void setIPAddress(InetAddress ipaddress){
        this.ipaddress = ipaddress;
    }
    public List<User> getContactList(){
        return this.contactList;
    }
    public void setContactList(List<User> contList){ this.contactList = contList;}
    public Boolean getState(){
        return this.state;
    }
    public void setState(Boolean state){
        this.state = state;
    }

    //add a user to the contact list
    public void addContact(User u){
        contactList.add(u);
    }
    //remove a user from the contact list
    public void deleteContact(User u){
        contactList.remove(u);
    }
    //checks if a user is in the contact list
    public Boolean containsContact(List<User> contactList, User u) {
        boolean contained = false;

        for (User v : contactList) {
            if (u.username.equals(v.username) && u.ipaddress.equals(v.ipaddress) && u.state.equals(v.state)) {
                contained = true;
                break;
            }
        }
        return contained;
    }
}
