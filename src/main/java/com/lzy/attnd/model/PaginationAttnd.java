package com.lzy.attnd.model;

public class PaginationAttnd {
    public Attnd[] getAttnds() {
        return attnds;
    }

    public void setAttnds(Attnd[] attnds) {
        this.attnds = attnds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private Attnd[] attnds;
    private int count;

    public PaginationAttnd(int count,Attnd[] attnds) {
        this.attnds = attnds==null?new Attnd[]{}:attnds;
        this.count = count<0?0:count;
    }
}
