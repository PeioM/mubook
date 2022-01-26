package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.api.AjaxController;
import com.libumu.mubook.entities.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    @Override
    public int countItemModelByIdentifier(String identifier) {
        return repository.countItemModelByIdentifier(identifier);
    }

    @Override
    public Long getTopId() {
        return repository.getTopId();
    }

    @Override
    public List<ItemModel> getItemModelsBySpecificationRowsBetween(List<Integer> specIds, List<String> specValues, int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getItemModelsWithFiltersBetween(specIds, specValues, start, quantity);
    }

    @Override
    public int getTotalItemModelFiltered(List<Integer> specIds, List<String> specValues, int itemTypeId) {
        List<Object[]> result = repository.getItemModelCountWithFilters(specIds, itemTypeId, specValues);
        BigInteger totalModels = (BigInteger) result.get(0)[0];
        return totalModels.intValue();
    }

    @Override
    public int getTotalItemModelByType(int itemTypeID) {
        List<Object[]> result = repository.getItemModelCountByItemType(itemTypeID);
        BigInteger totalModels = (BigInteger) result.get(0)[0];
        return totalModels.intValue();
    }

    @Override
    public int getTotalItemModels() {
        List<Object[]> result = repository.getTotalItemModelCount();
        BigInteger totalModels = (BigInteger) result.get(0)[0];
        return totalModels.intValue();
    }

    @Override
    public List<ItemModel> getAllItemModelsBetween(int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getItemModelsBetween(start, quantity);
    }

    @Override
    public List<ItemModel> getAllItemModelsByTypeAndBetween(int itemTypeId, int page) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getItemModelsByTypeAndBetween(itemTypeId, start, quantity);
    }
}
