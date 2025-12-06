package utils;

import model.Contact;
import model.User;

public class UndoData {
    private User oldUser;
    private Contact oldContact;
    private Action undoAction;

    public User getOldUser() {
        return oldUser;
    }

    public void setOldUser(User oldUser) {
        this.oldUser = oldUser;
    }

    public Contact getOldContact() {
        return oldContact;
    }

    public void setOldContact(Contact oldContact) {
        this.oldContact = oldContact;
    }

    public Action getUndoAction() {
        return undoAction;
    }

    public void setUndoAction(Action undoAction) {
        this.undoAction = undoAction;
    }
}
