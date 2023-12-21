package Model;

import java.util.ArrayList;
import java.util.List;

public class ContactList {


    private static final ContactList INSTANCE = new ContactList();


    static List<User> contacts = new ArrayList<>();

    public static List<User> getContacts()
    {
        return contacts;
    }

    public static void setContacts(List<User> c)
    {contacts = c;}
    private ContactList() {
    }

    public static void addContact(User u){
        contacts.add(u);
    }
    // Remove a user from the contact list
    public static void deleteContact(User u){
        contacts.remove(u);
    }



}
