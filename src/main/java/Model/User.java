package Model;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

public class User {
    private String username;
    private InetAddress ipaddress;
    public Boolean state;

    // Constructors
    public User(){
    }

    public User(String username){
        this.username = username;
    }

    public User(String username, InetAddress ipaddress){
        this.username = username;
        this.ipaddress = ipaddress;
    }

    public User(String username, InetAddress ipaddress, Boolean state){
        this.username = username;
        this.ipaddress = ipaddress;
        this.state = state;
    }
    // Getters and Setters for username, IPAddress, contact list and status
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

    public Boolean getState(){
        return this.state;
    }
    public void setState(Boolean state){
        this.state = state;
    }

    // Add a user to the contact list

    // Checks if a user is in the contact list
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



    @Override
    public int hashCode() {
        return Objects.hash(username, ipaddress, state);
    }

}
