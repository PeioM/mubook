package com.libumu.mubook.entities;

import com.libumu.mubook.dao.userActivity.UserActivityDataAccessService;
import com.libumu.mubook.dao.userType.UserTypeDataAccessService;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Date;

@Entity
@Table(name= "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long userId;

   /* @Column(name = "usertypeid")
    private String userTypeId;*/
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
    @Column(name = "dni_img")
    private String dniImgPath;
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "useractivityid")
    private int userActivityId;

    @ManyToOne
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "user_activity_id", nullable = false)
    private UserActivity userActivity;

    public User(HttpServletRequest request){
        this.userType = new UserTypeDataAccessService().getUserType("USER");
        this.userActivity = new UserActivityDataAccessService().getUserActivity(3);
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

    /*public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userType) {
        this.userTypeId = userType;
    }*/

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserActivity getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }
}
