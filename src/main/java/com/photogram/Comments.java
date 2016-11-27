package com.photogram;

import org.springframework.data.annotation.Id;

/**
 * Her er kommenteringsklassen. Den er knyttet til bildet.
 * Det settes krav til at følgende attributter skal være fylt ved innlegg: navn på bruker, kommentar, photoid, rolle (for kommentarfeltet), og fargen på rolle (for kommentarfeltet)
 */
public class Comments {
    @Id
    String id;
    String navn;
    String kommentar;
    String photoID;
    String brukerRolle;
    String brukerRolleFarge;


    public Comments(String navn, String kommentar, String photoID, String brukerRolle, String brukerRolleFarge) {
        this.navn = navn;
        this.kommentar = kommentar;
        this.photoID = photoID;
        this.brukerRolle = brukerRolle;
        this.brukerRolleFarge = brukerRolleFarge;
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

    public String getBrukerRolle() {
        return brukerRolle;
    }

    public void setBrukerRolle(String brukerRolle) {
        this.brukerRolle = brukerRolle;
    }

    public String getBrukerRolleFarge() {
        return brukerRolleFarge;
    }

    public void setBrukerRolleFarge(String brukerRolleFarge) {
        this.brukerRolleFarge = brukerRolleFarge;
    }
}
