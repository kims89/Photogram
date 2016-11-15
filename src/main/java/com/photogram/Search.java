package com.photogram;

/**
 * Created by kim on 15.11.2016.
 */
public class Search {
    String sok;
    String url;

    public Search(String sok, String url) {
        this.sok = sok;
        this.url = url;
    }

    public String getSok() {
        return sok;
    }

    public void setSok(String sok) {
        this.sok = sok;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
