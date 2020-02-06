package com.medium.progettomedium.Model;

public class DatabaseUtente {


    public String id;
    public String fullname;
    public String imageUrl;
    public String category;
    public String mail;
    public String pec;
    public String phone;
    public String data;
    public String luogo;
    public String residenza;
    public String indirizzo;
    public String cap;
    public String codiceAutorizzazione;
    public String cartaIdentita;

    public DatabaseUtente(){

    }

    public DatabaseUtente(String fullname, String mail,String id, String category) {
        this.fullname = fullname;
        this.mail = mail;
        this.id=id;
        this.category = category;
    }

    public DatabaseUtente(String fullname, String category, String pec, String phone, String data, String luogo, String residenza, String indirizzo, String cap, String codiceAutorizzazione, String cartaIdentita) {
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.category = category;
        this.pec = pec;
        this.phone = phone;
        this.data = data;
        this.luogo = luogo;
        this.residenza = residenza;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.codiceAutorizzazione = codiceAutorizzazione;
        this.cartaIdentita = cartaIdentita;
    }

    public DatabaseUtente(String fullname, String category, String pec, String phone, String data, String luogo, String residenza, String indirizzo, String cap) {
        this.fullname = fullname;
        this.category = category;
        this.pec = pec;
        this.phone = phone;
        this.data = data;
        this.luogo = luogo;
        this.residenza = residenza;
        this.indirizzo = indirizzo;
        this.cap = cap;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
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

    public String getCodiceAutorizzazione() {
        return codiceAutorizzazione;
    }

    public void setCodiceAutorizzazione(String codiceAutorizzazione) {
        this.codiceAutorizzazione = codiceAutorizzazione;
    }

    public String getCartaIdentita() {
        return cartaIdentita;
    }

    public void setCartaIdentita(String cartaIdentita) {
        this.cartaIdentita = cartaIdentita;
    }
}
