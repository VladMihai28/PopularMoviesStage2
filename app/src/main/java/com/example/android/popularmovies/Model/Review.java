package com.example.android.popularmovies.Model;

/**
 * Created by Vlad on 4/28/2018.
 */

public class Review {

    private String author;
    private String content;
    private String url;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
