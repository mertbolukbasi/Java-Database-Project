package model;

public class UndoOperation {

    public enum Type {
        ADD_CONTACT, DELETE_CONTACT, UPDATE_CONTACT,
        ADD_USER, DELETE_USER, UPDATE_USER
    }

    private Type type;

    private Contact oldContact;
    private Contact newContact;

    private User oldUser;
    private User newUser;

    private int id;

    public UndoOperation(Type type) {
        this.type = type;
    }

    public Type getType() { return type; }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public void setOldContact(Contact c) { this.oldContact = c; }
    public Contact getOldContact() { return oldContact; }

    public void setNewContact(Contact c) { this.newContact = c; }
    public Contact getNewContact() { return newContact; }

    public void setOldUser(User u) { this.oldUser = u; }
    public User getOldUser() { return oldUser; }

    public void setNewUser(User u) { return newUser; }
    public User getNewUser() { return newUser; }
}