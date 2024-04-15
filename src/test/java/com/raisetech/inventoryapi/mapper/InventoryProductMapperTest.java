package com.raisetech.inventoryapi.mapper;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql", "classpath:/delete-inventory-products.sql", "classpath:/insert-inventory-products.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class InventoryProductMapperTest {
    @Autowired
    InventoryProductMapper inventoryProductMapper;

    @Test
    @Transactional
    void 合計在庫数が取得できること() {
        int id = 1;
        Integer quantity = inventoryProductMapper.getQuantityByProductId(id);
        assertThat(quantity).isEqualTo(100);
    }

}
