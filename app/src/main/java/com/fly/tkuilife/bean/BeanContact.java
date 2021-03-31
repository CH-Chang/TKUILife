package com.fly.tkuilife.bean;

public class BeanContact {
    String name, telephone, extension, office;

    public BeanContact(String name, String telephone, String extension, String office){
        this.name = name;
        this.telephone = telephone;
        this.extension = extension;
        this.office = office;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
