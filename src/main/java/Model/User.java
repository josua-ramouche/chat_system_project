package Model;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class User {

    private String username;

    private InetAddress ipaddress; // User's IP address

    private List<User> contactList; // User's contact list

    public Boolean state; // User's state of connection : true = connected, false = disconnected

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

    public void setContactList(List<User> contList){ this.contactList = contList;}

    // Adds a user to contact list
    public void addContact(User u){
        contactList.add(u);
    }

    // Deletes a user from contact list
    public void deleteContact(User u){
        contactList.remove(u);
    }

    // Return true if a given user u is contained in the contact list
    public Boolean containsContact(List<User> contactList, User u) {
        Boolean contained = false;

        for (User v : contactList) {
            if (u.username.equals(v.username) && u.ipaddress.equals(v.ipaddress) && u.state.equals(v.state)) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    // Returns true if user is connected
    public Boolean getState(){
        return this.state;
    }

    // Changes user's state of connection
    public void setState(Boolean state){
        this.state = state;
    }


}
