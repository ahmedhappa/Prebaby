package com.usama.salamtek.Dashboard;

/**
 * Created by usama on 19/06/2018.
 */

public class Chart {
    private String name;
    private int thumbnail;

    public Chart(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
