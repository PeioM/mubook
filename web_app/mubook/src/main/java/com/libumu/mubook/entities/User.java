package com.libumu.mubook.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name= "user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "userid")
    private Long userId;

    @Column(name = "usertypeid")
    private int userTypeId;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "DNI")
    private String DNI;
    @Column(name = "borndate")
    private Date bornDate;
    @Column(name = "validated")
    private boolean validated;
    @Column(name = "dniimg")
    private String dniImgPath;

    public Long getUserId() {
        return userId;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userType) {
        this.userTypeId = userType;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDNI() {
        return DNI;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getDniImgPath() {
        return dniImgPath;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setDniImgPath(String dniImgPath) {
        this.dniImgPath = dniImgPath;
    }
}
