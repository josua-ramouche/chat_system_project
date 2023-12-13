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
    private ContactList() {
    }

    public static void addContact(User u){
        contacts.add(u);
    }
    // Remove a user from the contact list
    public void deleteContact(User u){
        contacts.remove(u);
    }



}
