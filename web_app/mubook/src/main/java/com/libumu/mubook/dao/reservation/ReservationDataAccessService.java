package com.libumu.mubook.dao.reservation;

import com.libumu.mubook.api.AjaxController;
import com.libumu.mubook.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    @Override
    public List<Reservation> findAllByItemItemModelNameAndUserUsername(String item_itemModel_name, String user_username) {
        return repository.findAllByItemItemModelNameAndUserUsername(item_itemModel_name, user_username);
    }

    @Override
    public List<Reservation> findAllByItemItemModelNameAndEndDateIsAfterAndUserUsername(String item_itemModel_name, Date endDate, String user_username) {
        return repository.findAllByItemItemModelNameAndEndDateIsAfterAndUserUsername(item_itemModel_name, endDate, user_username);
    }

    @Override
    public List<Long> getItemsWithoutReservation(long itemModelId) {
        return repository.getItemsWithoutReservation(itemModelId);
    }

    //Ajax functions
    @Override
    public List<Reservation> getReservationsByItemModelBetween(Long itemModel, int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findByItemModelBetween(itemModel, start,quantity);
    }

    @Override
    public List<Reservation> getReservationsByItemTypeBetween(Integer itemType, int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findByItemTypeBetween(itemType, start,quantity);
    }

    @Override
    public List<Reservation> getReservationsBetween(int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findBetween(start,quantity);
    }

    @Override
    public int getReservationCountByItemModel(Long itemModel) {
        List<Object[]> result = repository.countReservationsByItemModel(itemModel);
        BigInteger totalReservations = (BigInteger) result.get(0)[0];
        return totalReservations.intValue();
    }

    @Override
    public int getReservationCountByItemType(Integer itemType) {
        List<Object[]> result = repository.countReservationsByItemType(itemType);
        BigInteger totalReservations = (BigInteger) result.get(0)[0];
        return totalReservations.intValue();
    }

    @Override
    public int getTotalReservationCount() {
        Long result = repository.count();
        return result.intValue();
    }

    @Override
    public List<Reservation> getActiveReservationsBetween(int page) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsBetween(sqlDate, start, quantity);
    }

    @Override
    public List<Reservation> getActiveReservationsByItemTypeBetween(int itemType, int page) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsByItemTypeBetween(itemType, sqlDate, start, quantity);
    }

    @Override
    public List<Reservation> getActiveReservationsByItemModelBetween(long itemModel, int page) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsByItemModelBetween(itemModel, sqlDate, start, quantity);
    }

    @Override
    public int getTotalActiveReservationCount() {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqual(sqlDate, sqlDate);
    }

    @Override
    public int getActiveReservationCountByItemType(int itemType) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemTypeItemTypeId(sqlDate, sqlDate, itemType);
    }

    @Override
    public int getActiveReservationCountByItemModel(long itemModel) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemModelId(sqlDate, sqlDate, itemModel);
    }

    @Override
    public List<Reservation> getReservationsByItemModelBetweenForUser(Long itemModel, int page, long userId) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findByItemModelBetweenForUser(itemModel,start, quantity, userId);
    }

    @Override
    public List<Reservation> getReservationsByItemTypeBetweenForUser(Integer itemType, int page, long userId) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findByItemTypeBetweenForUser(itemType,start, quantity, userId);
    }

    @Override
    public List<Reservation> getReservationsBetweenForUser(int page, long userId) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.findBetweenForUser(start, quantity, userId);
    }

    @Override
    public int getTotalReservationCountForUser(long userId) {
        return repository.countAllByUserUserId(userId);
    }

    @Override
    public int getReservationCountByItemTypeForUser(int itemType, long userId) {
        return repository.countAllByItemItemModelItemTypeItemTypeIdAndUserUserId(itemType, userId);
    }

    @Override
    public int getReservationCountByItemModelForUser(long itemModel, long userId) {
        return repository.countAllByItemItemModelItemModelIdAndUserUserId(itemModel, userId);
    }

    @Override
    public List<Reservation> getActiveReservationsBetweenForUser(int page, long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsBetweenForUser(sqlDate, start, quantity, userId);
    }

    @Override
    public List<Reservation> getActiveReservationsByItemTypeBetweenForUser(int itemType, int page, long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsByItemTypeBetweenForUser(itemType, sqlDate, start, quantity, userId);
    }

    @Override
    public List<Reservation> getActiveReservationsByItemModelBetweenForUser(long itemModel, int page, long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getActiveReservationsByItemModelBetweenForUser(itemModel, sqlDate, start, quantity, userId);
    }

    @Override
    public int getTotalActiveReservationCountForUser(long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndUserUserId(
                sqlDate, sqlDate, userId);
    }

    @Override
    public int getActiveReservationCountByItemTypeForUser(int itemType, long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemTypeItemTypeIdAndUserUserId(
                sqlDate, sqlDate, itemType, userId);
    }

    @Override
    public int getActiveReservationCountByItemModelForUser(long itemModel, long userId) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new java.sql.Date(date.getTime());
        return repository.countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemModelIdAndUserUserId(
                sqlDate, sqlDate, itemModel, userId);
    }

}
