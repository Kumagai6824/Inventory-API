package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface InventoryProductMapper {
    @Select("SELECT * FROM inventoryProducts where product_id = #{product_id}")
    Optional<InventoryProduct> findInventoryByProductId(int productId);

    @Select("SELECT SUM(quantity) FROM inventoryProducts where product_id = #{product_id}")
    Optional<Integer> getQuantityByProductId(int productId);
}
