package com.photogram;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by kim on 09.11.2016.
 */
public class User {

    @Id
    String id;
    String fornavn;
    String etternavn;
    String brukernavn;
    String passord;
    String rolle;

    public User(String fornavn, String etternavn, String brukernavn, String passord, String rolle) {
        this.rolle = rolle;
        this.fornavn = fornavn;
        this.etternavn = etternavn;
        this.brukernavn = brukernavn;
        this.passord = passord;
    }

    public String getId() {
        return id;
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

    public String getPassord() {
        return passord;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }

    public String getRolle() {
        return rolle;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }
}
