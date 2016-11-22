package com.photogram;

import org.springframework.data.annotation.Id;

/**
 * Created by kim on 22.11.2016.
 */
public class Comments {
    @Id
    String id;
    String navn;
    String kommentar;
    String photoID;

    public Comments(String navn, String kommentar, String photoID) {
        this.navn = navn;
        this.kommentar = kommentar;
        this.photoID = photoID;
    }

    public String getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }
}
