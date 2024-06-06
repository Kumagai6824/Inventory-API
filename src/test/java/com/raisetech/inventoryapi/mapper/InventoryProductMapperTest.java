package com.raisetech.inventoryapi.mapper;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MybatisTest
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {"classpath:/reset-id.sql", "classpath:/delete-products.sql", "classpath:/insert-products.sql", "classpath:/reset-inventoryProductId.sql", "classpath:/delete-inventory-products.sql", "classpath:/insert-inventory-products.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class InventoryProductMapperTest {
    @Autowired
    InventoryProductMapper inventoryProductMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        logCurrentInventoryProducts("Before test");
    }

    @AfterEach
    void tearDown() {
        logCurrentInventoryProducts("After test");
    }

    @Test
    @Transactional
    @ExpectedDataSet(value = "/dataset/expectedInventoryProductId1.yml", ignoreCols = {"history"})
    void 商品IDと一致する在庫情報を取得できること() {
        int productId = 1;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(500);
        inventoryProductMapper.createInventoryProduct(inventoryProduct);

        int id = inventoryProduct.getId();
        assertNotNull(id);

        List<Optional<InventoryProduct>> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);
        assertNotNull(actualInventoryProducts);
        assertThat(actualInventoryProducts).hasSize(2);
    }

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
    @ExpectedDataSet(value = "/dataset/expectedCreatedInventoryProducts.yml", ignoreCols = {"history"})
    @Transactional
    void 登録した在庫情報と新しく採番されたIDが設定されること() {
        int productId = 1;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(500);
        inventoryProductMapper.createInventoryProduct(inventoryProduct);
    }

    private void logCurrentInventoryProducts(String phase) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM inventoryProducts");
        System.out.println(phase + " - Current inventoryProducts:");
        for (Map<String, Object> row : rows) {
            System.out.println(row);
        }
    }

}
