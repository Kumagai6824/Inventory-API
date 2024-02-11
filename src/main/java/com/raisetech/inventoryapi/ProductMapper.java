package com.raisetech.inventoryapi;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products")
    List<Product> findAll();

    @Select("SELECT * FROM products where id = #{id}")
    Optional<Product> findById(int id);

    @Insert("INSERT INTO products (name) values (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createProduct(Product product);

    @Update("UPDATE products SET name = #{name} WHERE id =#{id}")
    void updateProductById(int id, String name);

    @Delete("DELETE FROM products where id = #{id}")
    void deleteById(int id);

}
