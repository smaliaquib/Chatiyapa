package com.example.aquib.chatapp;

public class Conv {

    public boolean seen;
    public long time;

    public Conv(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Conv(boolean seen, long time) {
        this.seen = seen;
        this.time = time;
    }

}
