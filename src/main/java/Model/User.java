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

    // Checks if a user is in the contact list
    public Boolean containsContact(List<User> contactList, User u) {
        return contactList.stream().anyMatch(v -> v.equals(u));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(username, user.username) &&
                Objects.equals(ipaddress, user.ipaddress) &&
                Objects.equals(state, user.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, ipaddress, state);
    }

}
