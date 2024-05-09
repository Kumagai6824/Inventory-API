package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface InventoryProductMapper {
    @Select("SELECT * FROM inventoryProducts where product_id = #{product_id}")
    Optional<InventoryProduct> findInventoryByProductId(int productId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventoryProducts where product_id = #{product_id}")
    Integer getQuantityByProductId(int productId);
}
