package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationDataAccessService implements ReservationDao {

    @Autowired
    private ReservationRepository repository;

    @Override
    public List<Reservation> getAllReservations() {
        return (List<Reservation>) repository.findAll();
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
    
}
