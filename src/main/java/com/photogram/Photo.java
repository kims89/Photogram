package com.photogram;

import org.springframework.data.annotation.Id;

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
    String tag;
    String photographerID;


    public Photo(String filnavn, String contentType, String tittel, String beskrivelse, String dato, String tag) {
        this.filnavn = filnavn;
        this.contentType = contentType;
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
        this.dato = dato;
        this.tag = tag;
    }

    public Photo() {

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

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPhotographerID() {
        return photographerID;
    }

    public void setPhotographerID(String photographerID) {
        this.photographerID = photographerID;
    }
}
