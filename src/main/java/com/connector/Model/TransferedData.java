package com.connector.Model;

import java.io.Serializable;

public class TransferedData implements Serializable {
    public String action;
    public String data[];
    public boolean calling;

    public TransferedData(String action) {
        this.action = action;
        this.data = null;
    }

    public TransferedData(String action, boolean calling) {
        this.action = action;
        this.calling = calling;
        this.data = null;
    }

    public TransferedData(String action, String[] data) {
        this.action = action;
        this.data = data;
    }
}
