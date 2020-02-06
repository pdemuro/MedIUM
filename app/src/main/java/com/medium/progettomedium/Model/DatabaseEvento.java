package com.medium.progettomedium.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseEvento {
    public String id="";
    public String date="";
    public String titolo="";
    public Double latitude;
    public Double longitude;
    public String luogo="";
    public String immagine;
    public String descrizione;
    public Double distanza;
    public HashMap<String, String> prenotazioni;
    public String like;
    public DatabaseEvento(){

    }

    public static ArrayList<DatabaseEvento> date_collection_arr;
    public DatabaseEvento( String id,String date, String titolo, Double latitude, Double longitude, String luogo, HashMap<String,String> prenotazioni, String immagine, String descrizione,Double distanza){
        this.id=id;
        this.date=date;
        this.titolo=titolo;
        this.latitude=latitude;
        this.longitude=longitude;
        this.luogo = luogo;
        this.immagine = immagine;
        this.descrizione = descrizione;
        this.prenotazioni = prenotazioni;
        this.distanza = distanza;
    }
    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String mImageUrl) {
        this.immagine = mImageUrl;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static ArrayList<DatabaseEvento> getDate_collection_arr() {
        return date_collection_arr;
    }

    public static void setDate_collection_arr(ArrayList<DatabaseEvento> date_collection_arr) {
        DatabaseEvento.date_collection_arr = date_collection_arr;
    }
    public HashMap<String, String> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(HashMap<String, String> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    public Double getDistanza() {
        return distanza;
    }

    public void setDistanza(Double distanza) {
        this.distanza = distanza;
    }
}
