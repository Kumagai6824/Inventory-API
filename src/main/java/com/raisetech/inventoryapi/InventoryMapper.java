package com.raisetech.inventoryapi;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InventoryMapper {
    @Select("SELECT * FROM names")
    List<Inventory> findAll();

    @Select("SELECT * FROM names where id = #{id}")
    Optional<Inventory> findById(int id);

    @Insert("INSERT INTO names (name) values (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createName(Inventory inventory);

    @Update("UPDATE names SET name = #{name} WHERE id =#{id}")
    void patchById(int id, String name);

    @Delete("DELETE FROM names where id = #{id}")
    void deleteById(int id);

}
