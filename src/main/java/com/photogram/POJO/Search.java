package com.photogram.POJO;

/**
 * Til motsetning fra de andre POJO-klassene er denne klassen ikke knyttet til en database men heller blir initsiert når det blir gjort en forespørsel gjennom
 * livesøk for så at det blir presentert ut en JSON-liste med bildetittel, #tagg og fotografer.
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
