package Model;

import java.util.ArrayList;
import java.util.List;

public class ContactList {


    private static final ContactList INSTANCE = new ContactList();


    static List<User> contacts = new ArrayList<>();

    public static synchronized List<User> getContacts()
    {
        return contacts;
    }

    public static void setContacts(List<User> c)
    {contacts = c;}
    private ContactList() {
    }

    public static synchronized void addContact(User u){
        contacts.add(u);
    }
    // Remove a user from the contact list
    public static synchronized void deleteContact(User u){
        contacts.remove(u);
    }

    public static synchronized void printContactList(){
        System.out.println("Print Contact List :");
        contacts.forEach(u -> {
            if (u.getState()) {
                System.out.println("user : " + u.getUsername());
            }
        });
    }

}
