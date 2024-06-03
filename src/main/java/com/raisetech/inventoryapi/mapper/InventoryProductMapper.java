package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InventoryProductMapper {
    @Select("SELECT * FROM inventoryProducts where product_id = #{product_id}")
    List<Optional<InventoryProduct>> findInventoryByProductId(int productId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventoryProducts where product_id = #{product_id}")
    Integer getQuantityByProductId(int productId);

    @Insert("INSERT INTO inventoryProducts (product_id, quantity, history) values (#{productId}, #{quantity}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createInventoryProduct(InventoryProduct inventoryProduct);
}
