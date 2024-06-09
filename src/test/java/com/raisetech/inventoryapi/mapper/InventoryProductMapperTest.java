package com.raisetech.inventoryapi.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataSet(value = {"products.yml", "inventoryProducts.yml"}, executeScriptsBefore = {"reset-id.sql", "reset-inventoryProductId.sql"}, cleanAfter = true, transactional = true)
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

/*    @Test
    @Transactional
    void 商品IDと一致する在庫情報を取得できること() {
        int productId = 1;

        List<Optional<InventoryProduct>> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);
        assertThat(actualInventoryProducts).isNotNull();

        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        InventoryProduct expectedProduct = new InventoryProduct(1, 1, 100, offsetDateTime1);

        actualInventoryProducts.forEach(actual -> assertThat(actual)
                .isPresent()
                .contains(new InventoryProduct(1, 1, 100, offsetDateTime1)));
    }*/

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
