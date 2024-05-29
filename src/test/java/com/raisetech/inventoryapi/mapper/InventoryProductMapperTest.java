package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    @Transactional
    void 存在しない商品IDを指定したときに0を返すこと() {
        int id = 0;
        Integer quantity = inventoryProductMapper.getQuantityByProductId(id);
        assertThat(quantity).isEqualTo(0);
    }

    @Test
    @Transactional
    void 登録した在庫情報と新しく採番されたIDが設定されること() {
        int productId = 1;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(500);
        inventoryProductMapper.createInventoryProduct(inventoryProduct);

        int id = inventoryProduct.getId();
        OffsetDateTime expectedDateTime = inventoryProduct.getHistory();
        assertNotNull(id);
        assertThat(inventoryProductMapper.findInventoryByProductId(productId)).contains(new InventoryProduct(id, productId, 500, expectedDateTime));
    }

}
