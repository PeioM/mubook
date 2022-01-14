package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.Reservation;
import com.libumu.mubook.entities.User;

import java.sql.Date;
import java.util.List;

public interface ReservationDao {

    List<Reservation> getAllReservations();
    Reservation getReservation(long id);
    void editReservation(Reservation Reservation);
    void deleteReservation(long id);
    void deleteReservation(Reservation Reservation);
    void addReservation(Reservation Reservation);
    List<Object[]> countReservationsByItemType(int itemTypeId);
    List<Object[]> countReservationsByItemTypeWithoutMT();
    List<Object[]> countReservationsByItemModel(long itemModelId);
    List<Object[]> countReservationsByItemModelWithoutMT();
    List<Object[]> countReservationsOfItemEachMonth(long itemId);
    List<Object[]> countReservationsOfItemEachMonthWithoutMT(long itemModelId);
    List<Reservation> findAllByUserUsername(String user_username);
    List<Reservation> findAllByItemItemModelName(String item_itemModel_name);
    List<Reservation> findAllByEndDateIsAfter(Date date);
    List<Reservation> findAllByUserUsernameAndEndDateIsAfter(String user_username, Date date);
    List<Reservation> findAllByItemItemModelNameAndEndDateIsAfter(String item_itemModel_name, Date date);

}
