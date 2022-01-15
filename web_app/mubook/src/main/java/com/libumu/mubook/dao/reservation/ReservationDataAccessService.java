package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.Reservation;
import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ReservationDataAccessService implements ReservationDao {

    @Autowired
    private ReservationRepository repository;

    @Override
    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    @Override
    public Reservation getReservation(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editReservation(Reservation Reservation) {
        repository.save(Reservation);
    }

    @Override
    public void deleteReservation(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteReservation(Reservation Reservation) {
        repository.delete(Reservation);
    }

    @Override
    public void addReservation(Reservation Reservation) {
        repository.save(Reservation);
    }

    @Override
    public List<Object[]> countReservationsByItemType(int itemTypeId) {
        return repository.countReservationsByItemType(itemTypeId);
    }

    @Override
    public List<Object[]> countReservationsByItemModel(long itemModelId) {
        return repository.countReservationsByItemModel(itemModelId);
    }

    @Override
    public List<Object[]> countReservationsOfItemEachMonth(long itemId) {
        return repository.countReservationsOfItemEachMonth(itemId);
    }

    @Override
    public List<Object[]> countReservationsByItemTypeWithoutMT() {
        return repository.countReservationsByItemTypeWithoutMT();
    }

    @Override
    public List<Object[]> countReservationsByItemModelWithoutMT() {
        return repository.countReservationsByItemModelWithoutMT();
    }

    @Override
    public List<Object[]> countReservationsOfItemEachMonthWithoutMT(long itemModelId) {
        return repository.countReservationsOfItemEachMonthWithoutMT(itemModelId);
    }

    @Override
    public List<Reservation> findAllByUserUsername(String user_username) {
        return repository.findAllByUserUsername(user_username);
    }

    @Override
    public List<Reservation> findAllByItemItemModelName(String item_itemModel_name) {
        return repository.findAllByItemItemModelName(item_itemModel_name);
    }

    @Override
    public List<Reservation> findAllByEndDateIsAfter(Date date) {
        return repository.findAllByEndDateIsAfter(date);
    }

    @Override
    public List<Reservation> findAllByUserUsernameAndEndDateIsAfter(String user_username, Date date) {
        return repository.findAllByUserUsernameAndEndDateIsAfter(user_username, date);
    }

    @Override
    public List<Reservation> findAllByItemItemModelNameAndEndDateIsAfter(String item_itemModel_name, Date date) {
        return repository.findAllByItemItemModelNameAndEndDateIsAfter(item_itemModel_name, date);
    }

    @Override
    public List<Object[]> getFirstReservationDate(long item_model_id) {
        return repository.getFirstReservationDate(item_model_id);
    }

}
