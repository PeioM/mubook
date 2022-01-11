package com.libumu.mubook.dao.reservation;

import java.util.List;

import com.libumu.mubook.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT it.description, COUNT(r.reservation_id) "+
            "FROM item_type it "+
            "   JOIN item_model im on it.item_type_id = im.item_type_id "+
            "   JOIN item i on im.item_model_id = i.item_model_id "+
            "   JOIN reservation r on i.item_id = r.item_id "+
            "WHERE it.item_type_id = ?1", nativeQuery = true)
    public List<Object[]> countReservationsByItemType(int item_type_id);

    @Query(value = "SELECT it.description, COUNT(r.reservation_id) "+
            "FROM item_type it "+
            "   JOIN item_model im on it.item_type_id = im.item_type_id "+
            "   JOIN item i on im.item_model_id = i.item_model_id "+
            "   JOIN reservation r on i.item_id = r.item_id "+
            "WHERE it.item_type_id = 1 "+
            "UNION "+
            "SELECT it.description, COUNT(r.reservation_id) "+
            "FROM item_type it "+
            "   JOIN item_model im on it.item_type_id = im.item_type_id "+
            "   JOIN item i on im.item_model_id = i.item_model_id "+
            "   JOIN reservation r on i.item_id = r.item_id "+
            "WHERE it.item_type_id = 2", nativeQuery = true)
    public List<Object[]> countReservationsByItemTypeWithoutMT();

    @Query(value = "SELECT im.name, COUNT(r.reservation_id) as Cantidad "+
    "FROM item_model im "+
        "JOIN item i on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 1 "+
    "AND im.item_model_id = ?1", nativeQuery =  true)
    public List<Object[]> countReservationsByItemModel(long item_model_id);

    @Query(value = "SELECT im.name, COUNT(r.reservation_id) as Cantidad "+
    "FROM item_model im "+
        "JOIN item i on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 1 "+
    "GROUP BY im.item_model_id "+
    "ORDER BY Cantidad DESC "+
    "LIMIT 15", nativeQuery =  true)
    public List<Object[]> countReservationsByItemModelWithoutMT();

    @Query(value = "SELECT COUNT(r.reservation_id) AS num, date_format(r.init_date, '%Y-%M') AS Date "+
    "FROM item i "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 2 "+
    "AND i.item_id = ?1 "+
    "GROUP BY Date, i.item_id "+
    "ORDER BY Date", nativeQuery = true)
    public List<Object[]> countReservationsOfItemEachMonth(long item_id);

    @Query(value = "SELECT COUNT(r.reservation_id) AS num, date_format(r.init_date, '%Y-%M') AS Date "+
    "FROM item i "+
        "JOIN item_model im on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 2 "+
    "AND im.item_model_id = ?1 "+
    "GROUP BY Date "+
    "ORDER BY Date ", nativeQuery = true)
    public List<Object[]> countReservationsOfItemEachMonthWithoutMT(long item_model_id);
}
