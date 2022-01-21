package com.libumu.mubook.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "button_click")
public class ButtonClick {

    @Id
    @GenericGenerator(name="buttonClick" , strategy="increment")
    @GeneratedValue(generator="buttonClick")
    @Column(name = "button_click_id")
    private Long buttonClickId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "button_id")
    private Button button;

    @Column(name = "date")
    private Date date;

    public ButtonClick(){}
    public ButtonClick(Button button, User user){
        this.button = button;
        this.user = user;
        this.date = new Date();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getButtonClickId() {
        return buttonClickId;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
