package com.example.aquib.chatapp;

public class Friend_Req {

    String request_type;

    public Friend_Req(){

    }

    public Friend_Req(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
