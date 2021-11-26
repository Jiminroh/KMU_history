package com.example.teamproject2;

// firebase에서 받아온 reference를 하나씩 가져오기 위한 class
public class Information {

    private String id;
    private String pw;
    private String name;
    private String size;
    private String kinds;
    private String station;

    public Information() {
    }

    // 생성자
    public Information(String id, String pw, String name, String size, String kinds, String station) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.size = size;
        this.kinds = kinds;
        this.station = station;
    }

    // getmethod(), setmethod() 생성
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getKinds() {
        return kinds;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getPw() {
        return pw;
    }
    public void setPw(String pw) {
        this.pw = pw;
    }
}