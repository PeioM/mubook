package com.libumu.mubook.dao.reservation;

import java.util.List;

import com.libumu.mubook.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT it.name, COUNT(r.reservationId)"+
            "FROM item_type it"+
            "   JOIN item_model im on it.itemTypeId = im.itemTypeId"+
            "   JOIN item i on im.itemModelId = i.itemModelId"+
            "   JOIN reservation r on i.itemId = r.itemId"+
            "WHERE it.itemTypeId = ?1", nativeQuery = true)
    public List<Object[]> countReservationsByItemType(int itemTypeId);

    @Query(value = "SELECT it.name, COUNT(r.reservationId)"+
            "FROM item_type it"+
            "   JOIN item_model im on it.itemTypeId = im.itemTypeId"+
            "   JOIN item i on im.itemModelId = i.itemModelId"+
            "   JOIN reservation r on i.itemId = r.itemId"+
            "WHERE it.itemTypeId = 1"+
            "UNION"+
            "SELECT it.name, COUNT(r.reservationId)"+
            "FROM item_type it"+
            "   JOIN item_model im on it.itemTypeId = im.itemTypeId"+
            "   JOIN item i on im.itemModelId = i.itemModelId"+
            "   JOIN reservation r on i.itemId = r.itemId"+
            "WHERE it.itemTypeId = 2", nativeQuery = true)
    public List<Object[]> countReservationsByItemTypeWithoutMT();

    @Query(value = "SELECT im.name, COUNT(r.reservationId) as Cantidad"+
    "FROM item_model im"+
        "JOIN item i on im.itemModelId = i.itemModelId"+
        "JOIN reservation r on i.itemId = r.itemId"+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.initDate)/365) < 1"+
    "AND im.itemModelId = ?1", nativeQuery =  true)
    public List<Object[]> countReservationsByItemModel(long itemModelId);

    @Query(value = "SELECT im.name, COUNT(r.reservationId) as Cantidad"+
    "FROM item_model im"+
        "JOIN item i on im.itemModelId = i.itemModelId"+
        "JOIN reservation r on i.itemId = r.itemId"+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.initDate)/365) < 1"+
    "GROUP BY im.itemModelId"+
    "ORDER BY Cantidad DESC"+
    "LIMIT 15", nativeQuery =  true)
    public List<Object[]> countReservationsByItemModelWithoutMT();

    @Query(value = "SELECT COUNT(r.reservationId) AS num, date_format(r.initDate, '%Y-%M') AS Date"+
    "FROM item i"+
        "JOIN reservation r on i.itemId = r.itemId"+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.initDate)/365) < 2"+
    "AND i.itemId = ?1"+
    "GROUP BY Date, i.itemId"+
    "ORDER BY Date", nativeQuery = true)
    public List<Object[]> countReservationsOfItemEachMonth(long itemId);

    @Query(value = "SELECT COUNT(r.reservationId) AS num, date_format(r.initDate, '%Y-%M') AS Date"+
    "FROM item i"+
        "JOIN item_model im on im.itemModelId = i.itemModelId"+
        "JOIN reservation r on i.itemId = r.itemId"+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.initDate)/365) < 2"+
    "AND im.itemModelId = ?1"+
    "GROUP BY Date"+
    "ORDER BY Date", nativeQuery = true)
    public List<Object[]> countReservationsOfItemEachMonthWithoutMT(long itemModelId);
}
