package mx.tec.jobbify_enterprise.beans;

import java.util.ArrayList;

import mx.tec.jobbify_enterprise.enums.EnterpriseCategory;

/*
* Base class for a Enterprise object
*
* The Enterprise object will not hold it's own list of jobs
* because it will make the DB too deep, instead we're using a
* class that is responsible for handling all Job requests from the DB,
* see JobListHandler.java
*/
public class Enterprise {
    private String legalName;
    private String description;
    private String category;
    private String country;
    private ArrayList<String> phones;
    private ArrayList<String> addresses;
    //private Imagen Icon;

    public Enterprise() {
    }

    public Enterprise(String legalName, String description, String category, String country) {
        this.legalName = legalName;
        this.description = description;
        this.category = category;
        this.country = country;
        this.phones = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public void addPhone(String phone) {
        this.phones.add(phone);
    }

    public void addAddress(String address) {
        this.addresses.add(address);
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }
}
