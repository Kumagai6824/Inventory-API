package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InventoryProductMapper {
    @Select("SELECT * FROM inventoryProducts where product_id = #{product_id}")
    List<InventoryProduct> findInventoryByProductId(int productId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventoryProducts where product_id = #{product_id}")
    Integer getQuantityByProductId(int productId);

    @Insert("INSERT INTO inventoryProducts (product_id, quantity, history) values (#{productId}, #{quantity}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createInventoryProduct(InventoryProduct inventoryProduct);

    @Select("SELECT * FROM inventoryProducts where product_id = #{product_id} ORDER BY id desc LIMIT 1")
    Optional<InventoryProduct> findLatestInventoryByProductId(int productId);

    @Update("UPDATE inventoryProducts SET quantity = #{quantity} WHERE id =#{id}")
    void updateInventoryProductById(int id, int quantity);

    @Select("SELECT * FROM inventoryProducts where id = #{id}")
    Optional<InventoryProduct> findInventoryById(int id);

    @Delete("DELETE FROM inventoryProducts where id = #{id}")
    void deleteInventoryById(int id);
}
