package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products where deleted_at IS NULL")
    @Result(property = "deletedAt", column = "deleted_at")
    List<Product> findAll();

    @Select("SELECT * FROM products where id = #{id} and deleted_at IS NULL")
    @Result(property = "deletedAt", column = "deleted_at")
    Optional<Product> findById(int id);

    @Insert("INSERT INTO products (name) values (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createProduct(Product product);

    @Update("UPDATE products SET name = #{name} WHERE id =#{id}")
    void updateProductById(int id, String name);

    @Update("UPDATE products SET deleted_at = now() where id =#{id}")
    void deleteProductById(int id);

}
