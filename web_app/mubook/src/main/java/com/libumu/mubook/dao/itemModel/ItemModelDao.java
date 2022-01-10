package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.entities.ItemModel;

import java.util.List;

public interface ItemModelDao {

    public List<ItemModel> getAllItemModels();
    public ItemModel getItemModel(String id);
    public void editItemModel(ItemModel itemModel);
    public void deleteItemModel(String id);
    public void deleteItemModel(ItemModel itemModel);
    public void addItemModel(ItemModel itemModel);
    public List<Integer> getAllItemModelId();

}
