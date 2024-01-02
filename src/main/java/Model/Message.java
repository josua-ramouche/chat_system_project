package Model;

public class Message {
    private String content;
    private String date;
    private User sender;

    // Constructors
    public Message(){
    }

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


