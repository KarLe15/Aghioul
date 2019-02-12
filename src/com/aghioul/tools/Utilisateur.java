package com.aghioul.tools;

import java.util.Date;

public class Utilisateur {
    private String email;
    private String nom;
    private String prenom;
    private String password;
    private String userName;
    private String gender;
    private Date lastConnect;


    public Utilisateur(String email, String nom, String prenom, String password, String userName, String gender) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.userName = userName;
        this.gender = gender;

    }

    public Utilisateur() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getLastConnect() {
        return lastConnect;
    }

    public void setLastConnect(Date lastConnect) {
        this.lastConnect = lastConnect;
    }
}
