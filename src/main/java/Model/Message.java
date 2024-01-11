package Model;

public class Message {
    private final String content;
    private final String date;
    private final User sender;

    // Constructors
    public Message(String content, String date, User sender){
        this.content=content;
        this.date=date;
        this.sender=sender;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public User getSender() {
        return sender;
    }

}


