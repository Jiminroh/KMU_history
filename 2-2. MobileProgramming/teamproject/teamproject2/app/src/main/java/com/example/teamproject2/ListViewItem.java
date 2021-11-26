package com.example.teamproject2;

import android.graphics.drawable.Drawable;

// ListViewAdapter에서 item을 쉽게 가져오기 위한 class
public class ListViewItem {
    private Drawable iconDrawable ;
    private String idstr;
    private String namestr;
    private String sizestr;
    private String kindsstr;
    private String stationstr;

    // setmethod() 생성
    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setId(String id) {
        idstr = id ;
    }
    public void setName(String name) {
        namestr = name ;
    }
    public void setSize(String size) {
        sizestr = size ;
    }
    public void setKinds(String kinds) {
        kindsstr = kinds ;
    }
    public void setStation(String station) {
        stationstr = station ;
    }

    // getmethod() 생성
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getId() {
        return this.idstr ;
    }
    public String getName() {
        return this.namestr ;
    }
    public String getSize() {
        return this.sizestr ;
    }
    public String getKinds() {
        return this.kindsstr ;
    }
    public String getStation() {
        return this.stationstr ;
    }
}