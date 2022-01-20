package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.entities.ItemModel;

import java.util.List;

public interface ItemModelDao {
    List<ItemModel> getAllItemModels();
    List<ItemModel> getItemModelsByType(int itemTypeId);
    ItemModel getItemModel(long id);
    void editItemModel(ItemModel itemModel);
    void deleteItemModel(long id);
    void deleteItemModel(ItemModel itemModel);
    void addItemModel(ItemModel itemModel);
    List<Object[]> getAllItemModelId();
    int countItemModelByIdentifierAndItemModelIdNotLike(String identifier, Long itemModelId);
    List<ItemModel> getItemModelsBySpecification(List<Long> ids, int specId, String specValue);
    List<ItemModel> getItemModelsBySpecificationRowsBetween(List<Integer> specIds, List<String> specValues, int page);
    int getTotalItemModelFiltered(List<Integer> specIds, List<String> specValues, int itemTypeId);

    int getTotalItemModelByType(int itemTypeID);
    List<ItemModel> getAllItemModelsByTypeAndBetween(int itemTypeId, int page);
}
