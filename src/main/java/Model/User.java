package Model;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class User {

    private String username;

    private InetAddress ipaddress;

    private List<User> contactList;

    public Boolean state;

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

    public void addContact(User u){
        contactList.add(u);
    }

    public void deleteContact(User u){
        contactList.remove(u);
    }

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

    public Boolean getState(){
        return this.state;
    }

    public void setState(Boolean state){
        this.state = state;
    }


}
