package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.entities.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemModelDataAccessService implements ItemModelDao {

    @Autowired
    private ItemModelRepository repository;

    @Override
    public List<ItemModel> getAllItemModels() {
        return (List<ItemModel>) repository.findAll();
    }

    @Override
    public List<ItemModel> getItemModelsByType(int itemTypeId) {
        return repository.findAllByItemTypeItemTypeId(itemTypeId);
    }

    @Override
    public ItemModel getItemModel(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editItemModel(ItemModel itemModel) {
        repository.save(itemModel);
    }

    @Override
    public void deleteItemModel(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteItemModel(ItemModel itemModel) {
        repository.delete(itemModel);
    }

    @Override
    public void addItemModel(ItemModel itemModel) {
        repository.save(itemModel);
    }

    @Override
    public List<Object[]> getAllItemModelId() {
        return repository.getAllItemModelId();
    }

    @Override
    public int countItemModelByIdentifierAndItemModelIdNotLike(String identifier, Long itemModelId) {
        return repository.countItemModelByIdentifierAndItemModelIdNotLike(identifier, itemModelId);
    }

    @Override
    public List<ItemModel> getItemModelsBySpecification(List<Long> ids, int specId, String specValue) {
        return repository.findAllByItemModelIdInAndSpecificationListsSpecificationSpecificationIdAndSpecificationListsValue(ids, specId, specValue);
    }

}
