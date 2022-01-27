package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecificationRepository extends JpaRepository<Specification, Integer> {
    @Query(value = "SELECT specification_id FROM specification", nativeQuery = true)
    List<Object[]> getAllSpecificationId();

    @Query(value = "SELECT DISTINCT sl.specification_id, sl.value " +
                    "FROM item_model im " +
                    "    JOIN specification_list sl on im.item_model_id = sl.item_model_id " +
                    "WHERE im.item_type_id = ?1", nativeQuery = true)
    List<Object[]> getAllSpecificationAndValuesByItemType(int itemTypeId);

    Specification findSpecificationBySpecificationIdIs(Integer specificationId);
}
