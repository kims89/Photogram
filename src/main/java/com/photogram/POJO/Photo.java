package com.photogram.POJO;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by kim on 09.11.2016.
 */
public class Photo {

    @Id
    String id;
    String filnavn;
    String contentType;
    String tittel;
    String dato;
    String beskrivelse;
    List tag;
    List<Comments> kommentarer;
    String photographerID;


    public Photo(String filnavn, String contentType, String tittel, String beskrivelse, String dato) {
        this.filnavn = filnavn;
        this.contentType = contentType;
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
        this.dato = dato;
    }

    public Photo() {

    }


    public List<Comments> getKommentarer() {
        return kommentarer;
    }

    public void setKommentarer(List<Comments> kommentarer) {
        this.kommentarer = kommentarer;
    }

    public String getFilnavn() {
        return filnavn;
    }

    public void setFilnavn(String filnavn) {
        this.filnavn = filnavn;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getId() {
        return id;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {this.beskrivelse = beskrivelse; }

    public List getTag() {
        return tag;
    }

    public void setTag(List tag) {
        this.tag = tag;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getPhotographerID() {
        return photographerID;
    }

    public void setPhotographerID(String photographerID) {
        this.photographerID = photographerID;
    }
}
