package model;

public class undoOperation {

    public enum ActionType {
        ADD_CONTACT,
        UPDATE_CONTACT,
        DELETE_CONTACT,
        ADD_USER,
        DELETE_USER,
        UPDATE_USER
    }

    private ActionType type;
    private Object oldData;
    private Object newData;

    public undoOperation(ActionType type, Object oldData, Object newData) {
        this.type = type;
        this.oldData = oldData;
        this.newData = newData;
    }

    public ActionType getType() {
        return type;
    }

    public Object getOldData() {
        return oldData;
    }

    public Object getNewData() {
        return newData;
    }
}
