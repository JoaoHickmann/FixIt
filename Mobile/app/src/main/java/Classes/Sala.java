package Classes;

import java.io.Serializable;

public class Sala implements Serializable {
    private static final long serialVersionUID = 1003L;
    private int ID;
    
    public Sala(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}