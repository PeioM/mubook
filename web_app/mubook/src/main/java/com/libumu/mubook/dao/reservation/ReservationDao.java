package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.entities.Reservation;

import java.util.List;

public interface ReservationDao {

    public List<Reservation> getAllReservations();
    public Reservation getReservation(long id);
    public void editReservation(Reservation Reservation);
    public void deleteReservation(long id);
    public void deleteReservation(Reservation Reservation);
    public void addReservation(Reservation Reservation);
    public List<Object[]> countReservationsByItemType(int itemTypeId);
    public List<Object[]> countReservationsByItemModel(long itemModelId);
    public List<Object[]> countReservationsOfItemEachMonth(long itemId);

}
