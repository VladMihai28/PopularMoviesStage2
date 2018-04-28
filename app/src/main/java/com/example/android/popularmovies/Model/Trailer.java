package com.example.android.popularmovies.Model;

/**
 * Created by Vlad on 4/28/2018.
 */

public class Trailer {

    private String site;
    private String key;
    private String type;
    private String name;

    public void setSite(String site) {
        this.site = site;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
