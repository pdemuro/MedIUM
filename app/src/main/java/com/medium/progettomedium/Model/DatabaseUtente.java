package com.medium.progettomedium.Model;

import java.util.ArrayList;

public class DatabaseUtente {


    public String id;
    public String nome;
    public String cognome;
    public String imageUrl;
    public String category;
    public String mail;
    public String phone;
    public String data;
    public String luogo;
    public String residenza;
    public String indirizzo;
    public String cap;



    public DatabaseUtente(){

    }

    public static ArrayList<DatabaseUtente> date_collection_arr;
    public DatabaseUtente(String nome, String cognome, String mail,String id, String category,String imageUrl) {
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.id=id;
        this.category = category;
        this.imageUrl =imageUrl;
    }

    public DatabaseUtente(String nome,String cognome, String mail, String id, String category, String phone, String data, String luogo, String residenza, String indirizzo, String cap,String imageUrl) {
        this.nome = nome;
        this.cognome = cognome;
        this.category = category;
        this.mail = mail;
        this.id  = id;
        this.phone = phone;
        this.data  = data;
        this.luogo = luogo;
        this.residenza=residenza;
        this.indirizzo = indirizzo;
        this.cap = cap;

        this.imageUrl =imageUrl;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getResidenza() {
        return residenza;
    }

    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }
}
