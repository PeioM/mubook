package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.entities.ItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ItemModelRepository extends JpaRepository<ItemModel, Long> {
    int countItemModelByIdentifierAndItemModelIdNotLike(String identifier, Long itemModelId);

    @Query(value = "SELECT item_model_id FROM item_model", nativeQuery = true)
    List<Object[]> getAllItemModelId();
    List<ItemModel> findAllByItemTypeItemTypeId(Integer itemType_itemTypeId);
    List<ItemModel> findAllByItemModelIdInAndSpecificationListsSpecificationSpecificationIdAndSpecificationListsValue(Collection<Long> itemModelId, Integer specificationLists_specification_specificationId, String specificationLists_value);

    @Query(value =  "SELECT DISTINCT i.* " +
                    "FROM item_model i " +
                    "WHERE i.item_type_id = ?1 " +
                    "LIMIT ?2,?3" , nativeQuery = true)
    List<ItemModel> getItemModelsByTypeBetween(int itemTypeId, int startRow, int endRow);

    @Query(value =  "SELECT DISTINCT i.*" +
                    "FROM item_model i" +
                    "    JOIN specification_list sl on i.item_model_id = sl.item_model_id AND sl.specification_id IN ?1 " +
                    "WHERE sl.value IN ?2 " +
                    "LIMIT ?3,?4" , nativeQuery = true)
    List<ItemModel> getItemModelsWithFiltersBetween(List<Integer> specIds, List<String> specValues, int startRow, int endRow);

    @Query(value =  "SELECT COUNT(DISTINCT i.item_model_id) "+
                    "FROM item_model i" +
                    "    JOIN specification_list sl on i.item_model_id = sl.item_model_id AND sl.specification_id IN ?1 " +
                    "WHERE i.item_type_id = ?2 AND sl.value IN ?3 " , nativeQuery = true)
    List<Object[]> getItemModelCountWithFilters(List<Integer> specIds, int itemTypeId ,List<String> specValues);

    @Query(value =  "SELECT COUNT(DISTINCT i.item_model_id) " +
                    "FROM item_model i " +
                    "WHERE item_type_id = ?1" , nativeQuery = true)
    List<Object[]> getItemModelCountByItemType(int itemTypeId);
}
