package com.libumu.mubook.dao.reservation;

import java.sql.Date;
import java.util.List;

import com.libumu.mubook.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByEndDateIsAfter(Date date);
    List<Reservation> findAllByUserUsername(String user_username);
    List<Reservation> findAllByItemItemModelName(String item_itemModel_name);
    List<Reservation> findAllByUserUsernameAndEndDateIsAfter(String user_username, Date date);
    List<Reservation> findAllByItemItemModelNameAndEndDateIsAfter(String item_itemModel_name, Date date);
    List<Reservation> findAllByItemItemModelNameAndUserUsername(String item_itemModel_name, String user_username);
    List<Reservation> findAllByItemItemModelNameAndEndDateIsAfterAndUserUsername(String item_itemModel_name, Date endDate, String user_username);

    @Query(value = "SELECT MAX(r2.end_date) fecha, i2.item_id " +
            "    FROM reservation r2 "+
            "        JOIN item i2 on i2.item_id = r2.item_id " +
            "        JOIN item_model m on m.item_model_id = i2.item_model_id " +
            "        JOIN status s on s.status_id = i2.status_id " +
            "    WHERE m.item_model_id = ?1" +
            "    AND s.description = 'Available' " +
            "GROUP BY i2.item_id " +
            "ORDER BY fecha ASC " +
            "LIMIT 1", nativeQuery = true)
    List<Object[]> getFirstReservationDate(long item_model_id);

    @Query(value = "SELECT item_id "+
                        "FROM item "+
                            "JOIN item_model m on m.item_model_id = item.item_model_id "+
                            "JOIN status s on s.status_id = item.status_id "+
                        "WHERE item_id NOT IN(select DISTINCT (i.item_id) " +
                                            "from reservation "+
                                                "join item i on i.item_id = reservation.item_id "+
                                                "join item_model im on im.item_model_id = i.item_model_id "+
                                            "where im.item_model_id = ?1) "+
                        "AND m.item_model_id = ?1 " +
                        "AND s.description = 'Available' ", nativeQuery = true)
    List<Long> getItemsWithoutReservation(long itemModelId);

    @Query(value = "SELECT it.description, COUNT(r.reservation_id) "+
            "FROM item_type it "+
            "   JOIN item_model im on it.item_type_id = im.item_type_id "+
            "   JOIN item i on im.item_model_id = i.item_model_id "+
            "   JOIN reservation r on i.item_id = r.item_id "+
            "WHERE it.item_type_id = ?1", nativeQuery = true)
    List<Object[]> countReservationsByItemType(int item_type_id);

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
    List<Object[]> countReservationsByItemTypeWithoutMT();

    @Query(value = "SELECT im.name, COUNT(r.reservation_id) as Cantidad "+
    "FROM item_model im "+
        "JOIN item i on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 1 "+
    "AND im.item_model_id = ?1", nativeQuery =  true)
    List<Object[]> countReservationsByItemModel(long item_model_id);

    @Query(value = "SELECT im.name, COUNT(r.reservation_id) as Cantidad "+
    "FROM item_model im "+
        "JOIN item i on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 1 "+
    "GROUP BY im.item_model_id "+
    "ORDER BY Cantidad DESC "+
    "LIMIT 15", nativeQuery =  true)
    List<Object[]> countReservationsByItemModelWithoutMT();

    @Query(value = "SELECT COUNT(r.reservation_id) AS num, YEAR(r.init_date) AS Year, MONTH(r.init_date) AS Month "+
    "FROM item i "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 2 "+
    "AND i.item_id = ?1 "+
    "GROUP BY Year, Month, i.item_id "+
    "ORDER BY Year, Month", nativeQuery = true)
    List<Object[]> countReservationsOfItemEachMonth(long item_id);

    @Query(value = "SELECT COUNT(r.reservation_id) AS num, YEAR(r.init_date) AS Year, MONTH(r.init_date) AS Month "+
    "FROM item i "+
        "JOIN item_model im on im.item_model_id = i.item_model_id "+
        "JOIN reservation r on i.item_id = r.item_id "+
    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, r.init_date)/365) < 2 "+
    "AND im.item_model_id = ?1 "+
    "GROUP BY Year, Month "+
    "ORDER BY Year, Month ", nativeQuery = true)
    List<Object[]> countReservationsOfItemEachMonthWithoutMT(long item_model_id);

    //All reservations count for admin
    int countAllByItemItemModelItemModelId(Long item_itemModel_itemModelId);
    int countAllByItemItemModelItemTypeItemTypeId(Integer item_itemModel_itemType_itemTypeId);
    long count();
    //Active reservations count for admin
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqual(Date endDate, Date initDate);
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemModelId(Date endDate, Date initDate, Long item_itemModel_itemModelId);
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemTypeItemTypeId(Date endDate, Date initDate, Integer item_itemModel_itemType_itemTypeId);
    //Active reservations count for specific user
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndUserUserId(Date endDate, Date initDate, Long user_userId);
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemModelIdAndUserUserId(Date endDate, Date initDate, Long item_itemModel_itemModelId, Long user_userId);
    int countAllByEndDateGreaterThanEqualAndInitDateLessThanEqualAndItemItemModelItemTypeItemTypeIdAndUserUserId(Date endDate, Date initDate, Integer item_itemModel_itemType_itemTypeId, Long user_userId);
    //All reservations count for specific user
    int countAllByUserUserId(Long user_userId);
    int countAllByItemItemModelItemModelIdAndUserUserId(Long item_itemModel_itemModelId, Long user_userId);
    int countAllByItemItemModelItemTypeItemTypeIdAndUserUserId(Integer item_itemModel_itemType_itemTypeId, Long user_userId);


    //All reservations for admin
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id " +
            "WHERE i.item_model_id = ?1 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> findByItemModelBetween(long itemModel, int startRow, int quantity);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id " +
            "JOIN item_model im on i.item_model_id = im.item_model_id " +
            "WHERE im.item_type_id = ?1 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> findByItemTypeBetween(int itemType, int startRow, int quantity);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?1,?2", nativeQuery = true)
    List<Reservation> findBetween(int startRow, int quantity);

    //Active reservations for admin
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "WHERE r.init_date <= ?1 AND r.end_date >= ?1 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> getActiveReservationsBetween(Date sqlDate, int start, int quantity);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id " +
            "JOIN item_model im on i.item_model_id = im.item_model_id " +
            "WHERE im.item_type_id = ?1 " +
            "AND r.init_date <= ?2 AND r.end_date >= ?2 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?3,?4", nativeQuery = true)
    List<Reservation> getActiveReservationsByItemTypeBetween(int itemType, Date sqlDate, int start, int quantity);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id " +
            "WHERE i.item_model_id = ?1 " +
            "AND r.init_date <= ?2 AND r.end_date >= ?2 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?3,?4", nativeQuery = true)
    List<Reservation> getActiveReservationsByItemModelBetween(long itemModel, Date sqlDate, int start, int quantity);

    //All reservations for specific user
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id AND r.user_id = ?4 " +
            "WHERE i.item_model_id = ?1 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> findByItemModelBetweenForUser(long itemModel, int startRow, int quantity, long userID);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id AND r.user_id = ?4 " +
            "JOIN item_model im on i.item_model_id = im.item_model_id " +
            "WHERE im.item_type_id = ?1 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> findByItemTypeBetweenForUser(int itemType, int startRow, int quantity, long userID);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "WHERE r.user_id = ?3 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?1,?2", nativeQuery = true)
    List<Reservation> findBetweenForUser(int startRow, int quantity, long userID);

    //Active reservations for specific user
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "WHERE r.init_date <= ?1 AND r.end_date >= ?1 AND r.user_id = ?4 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?2,?3", nativeQuery = true)
    List<Reservation> getActiveReservationsBetweenForUser(Date sqlDate, int start, int quantity, long userID);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id AND r.user_id = ?5 " +
            "JOIN item_model im on i.item_model_id = im.item_model_id " +
            "WHERE im.item_type_id = ?1 " +
            "AND r.init_date <= ?2 AND r.end_date >= ?2 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?3,?4", nativeQuery = true)
    List<Reservation> getActiveReservationsByItemTypeBetweenForUser(int itemType, Date sqlDate, int start, int quantity, long userID);
    @Query(value = "SELECT r.* " +
            "FROM reservation r " +
            "JOIN item i on r.item_id = i.item_id AND r.user_id = ?5 " +
            "WHERE i.item_model_id = ?1 " +
            "AND r.init_date <= ?2 AND r.end_date >= ?2 " +
            "ORDER BY r.init_date DESC " +
            "LIMIT ?3,?4 ", nativeQuery = true)
    List<Reservation> getActiveReservationsByItemModelBetweenForUser(long itemModel, Date sqlDate, int start, int quantity, long userID);
}
