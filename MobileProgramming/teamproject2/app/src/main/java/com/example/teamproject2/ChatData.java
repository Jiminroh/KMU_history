package com.example.teamproject2;

// 대화창에서 name, message를 가져오기 위한 class
public class ChatData {
    private String userName;
    private String message;

    public ChatData() { }

    //생성자
    public ChatData(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    // setmethod(), getmethod() 생성
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}