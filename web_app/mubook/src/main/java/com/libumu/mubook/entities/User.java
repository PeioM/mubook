package com.libumu.mubook.entities;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;

@Entity
@Table(name= "user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "userid")
    private Long userId;

    @Column(name = "usertypeid")
    private String userTypeId;
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
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "useractivityid")
    private int userActivityId;

    public User(HttpServletRequest request){
        this.userTypeId = "USER";
        this.name = (String) request.getAttribute("name");
        this.surname = (String) request.getAttribute("surname");
        this.email = (String) request.getAttribute("email");
        this.DNI = (String) request.getAttribute("dni");
        this.username = (String) request.getAttribute("username");
        this.password = String.valueOf(request.getAttribute("password").hashCode());
        this.bornDate = (Date) request.getAttribute("birthDate");
        this.dniImgPath = ((File) request.getAttribute("dniImg")).getPath();
    }

    public User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userType) {
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

    public int getUserActivityId() {
        return userActivityId;
    }

    public void setUserActivityId(int userActivityId) {
        this.userActivityId = userActivityId;
    }
}
