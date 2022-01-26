package com.libumu.mubook.entitiesAsClasses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.libumu.mubook.entities.*;

import java.sql.Date;

public class ReservationClass {
    private Long reservationId;
    private UserClass user;
    private ItemClass item;
    private Long initDate;
    private Long endDate;
    private Date returnDate;

    public ReservationClass(Reservation reservation){
        this.reservationId = reservation.getReservationId();
        this.user = new UserClass(reservation.getUser());
        this.item = new ItemClass(reservation.getItem());
        this.initDate = reservation.getInitDate().getTime();
        this.endDate = reservation.getEndDate().getTime();
        this.returnDate = reservation.getReturnDate();
    }

    public Long getEndDate() {
        return endDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Long getInitDate() {
        return initDate;
    }

    public ItemClass getItem() {
        return item;
    }

    public UserClass getUser() {
        return user;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public static class UserClass{
        private Long userId;
        private String name;
        private String surname;
        private String DNI;
        private Date bornDate;
        private boolean validated;
        private String dniImgPath;
        private String email;
        private String username;
        private String password;

        public UserClass(User user) {
            this.userId = user.getUserId();
            this.name = user.getName();
            this.surname = user.getSurname();
            this.DNI = user.getDNI();
            this.bornDate = user.getBornDate();
            this.validated = user.isValidated();
            this.dniImgPath = user.getDniImgPath();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.password = user.getPassword();
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

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class ItemClass{
        private Long itemId;
        private String serialNum;
        private ItemModelClass itemModel;
        private Status status;

        public ItemClass(Item item) {
            this.itemId = item.getItemId();
            this.serialNum = item.getSerialNum();
            this.itemModel = new ItemModelClass(item.getItemModel());
            this.status = item.getStatus();
        }

        public Long getItemId() {
            return itemId;
        }

        public String getSerialNum() {
            return serialNum;
        }

        public ItemModelClass getItemModel() {
            return itemModel;
        }

        public Status getStatus() {
            return status;
        }
    }
}
