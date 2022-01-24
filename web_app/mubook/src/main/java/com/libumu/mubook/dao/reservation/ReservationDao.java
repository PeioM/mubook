package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entities.Reservation;

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
    List<Object[]> getFirstReservationDate(long item_model_id);
    List<Reservation> findAllByItemItemModelNameAndUserUsername(String item_itemModel_name, String user_username);
    List<Reservation> findAllByItemItemModelNameAndEndDateIsAfterAndUserUsername(String item_itemModel_name, Date endDate, String user_username);
    List<Long> getItemsWithoutReservation(long itemModelId);

    //Ajax functions for admin
    //All
    List<Reservation> getReservationsByItemModelBetween(Long itemModel, int page);
    List<Reservation> getReservationsByItemTypeBetween(Integer itemType, int page);
    List<Reservation> getReservationsBetween(int page);
    int getReservationCountByItemModel(Long itemModel);
    int getReservationCountByItemType(Integer itemType);
    int getTotalReservationCount();
    //Active
    List<Reservation> getActiveReservationsBetween(int page);
    List<Reservation> getActiveReservationsByItemTypeBetween(int itemType, int page);
    List<Reservation> getActiveReservationsByItemModelBetween(long itemModel, int page);
    int getTotalActiveReservationCount();
    int getActiveReservationCountByItemType(int itemType);
    int getActiveReservationCountByItemModel(long itemModel);
    //Ajax functions for normal user
    //All
    List<Reservation> getReservationsByItemModelBetweenForUser(Long itemModel, int page, long userId);
    List<Reservation> getReservationsByItemTypeBetweenForUser(Integer itemType, int page, long userId);
    List<Reservation> getReservationsBetweenForUser(int page, long userId);
    int getTotalReservationCountForUser(long userId);
    int getReservationCountByItemTypeForUser(int itemType, long userId);
    int getReservationCountByItemModelForUser(long itemModel, long userId);
    //Active
    List<Reservation> getActiveReservationsBetweenForUser(int page, long userId);
    List<Reservation> getActiveReservationsByItemTypeBetweenForUser(int itemType, int page, long userId);
    List<Reservation> getActiveReservationsByItemModelBetweenForUser(long itemModel, int page, long userId);
    int getTotalActiveReservationCountForUser(long userId);
    int getActiveReservationCountByItemTypeForUser(int itemType, long userId);
    int getActiveReservationCountByItemModelForUser(long itemModel, long userId);
}
