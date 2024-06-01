package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

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
        assertNotNull(id);

        List<InventoryProduct> actualInventoryProducts = inventoryProductMapper.findInventoryByProductIdForTest(productId);

        OffsetDateTime expectedDateTime1 = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        OffsetDateTime expectedDateTime2 = actualInventoryProducts.get(1).getHistory();

        assertNotNull(expectedDateTime2);
        assertThat(actualInventoryProducts)
                .hasSize(2)
                .contains(
                        new InventoryProduct(1, productId, 100, expectedDateTime1),
                        new InventoryProduct(id, productId, 500, expectedDateTime2)
                );
    }

}
