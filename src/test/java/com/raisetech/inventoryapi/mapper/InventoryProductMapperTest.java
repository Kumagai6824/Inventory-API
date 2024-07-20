package com.raisetech.inventoryapi.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MybatisTest
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataSet(value = {"products.yml", "inventoryProducts.yml"}, executeScriptsBefore = {"reset-id.sql", "reset-inventoryProductId.sql"}, cleanAfter = true, transactional = true)
class InventoryProductMapperTest {
    @Autowired
    InventoryProductMapper inventoryProductMapper;

    @Test
    @Transactional
    void 指定した商品IDの在庫情報を取得できること() {
        int productId = 3;
        List<InventoryProduct> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);

        OffsetDateTime dateTime = OffsetDateTime.parse("2024-05-10T12:58:10+09:00");
        OffsetDateTime dateTime2 = OffsetDateTime.parse("2024-05-11T12:58:10+09:00");

        assertThat(actualInventoryProducts)
                .hasSize(2)
                .containsExactly(
                        new InventoryProduct(3, productId, 500, dateTime),
                        new InventoryProduct(4, productId, -500, dateTime2)
                );
    }

    @Test
    @Transactional
    void 指定した商品IDの在庫が未登録のとき空を返すこと() {
        int productId = 4;
        List<InventoryProduct> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);
        Assertions.assertNotNull(actualInventoryProducts);
        assertThat(actualInventoryProducts).isEmpty();
    }

    @Test
    @Transactional
    void 存在しない商品IDで在庫取得処理したとき空を返すこと() {
        int productId = 0;
        List<InventoryProduct> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);
        Assertions.assertNotNull(actualInventoryProducts);
        assertThat(actualInventoryProducts).isEmpty();
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
    @Transactional
    void 登録した在庫情報と新しく採番されたIDが設定されること() {
        int productId = 1;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(500);
        inventoryProductMapper.createInventoryProduct(inventoryProduct);

        int id = inventoryProduct.getId();
        Assertions.assertNotNull(id);

        List<InventoryProduct> actualInventoryProducts = inventoryProductMapper.findInventoryByProductId(productId);

        OffsetDateTime dateTime1 = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        OffsetDateTime dateTime2 = actualInventoryProducts.get(1).getHistory();

        Assertions.assertNotNull(dateTime2);
        assertThat(actualInventoryProducts)
                .hasSize(2)
                .containsExactly(new InventoryProduct(1, productId, 100, dateTime1),
                        new InventoryProduct(id, productId, 500, dateTime2));

    }

    @Test
    @ExpectedDataSet(value = "/inventoryProducts.yml")
    @Transactional
    void 存在しない商品IDで在庫登録時登録されないこと() {
        int productId = 0;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(500);
        assertThatThrownBy(() -> inventoryProductMapper.createInventoryProduct(inventoryProduct)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Transactional
    void 指定した商品IDで最後に登録された在庫を返すこと() {
        int productId = 3;
        Optional<InventoryProduct> actualInventoryProduct = inventoryProductMapper.findLatestInventoryByProductId(productId);

        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-05-11T12:58:10+09:00");
        Optional<InventoryProduct> expectedInventoryProduct = Optional.of(new InventoryProduct(4, 3, -500, offsetDateTime));

        assertThat(actualInventoryProduct).isEqualTo(expectedInventoryProduct);
    }

    @Test
    @Transactional
    void 存在しない商品IDで最後に登録された在庫取得時に空を返すこと() {
        int productId = 0;
        Optional<InventoryProduct> actualInventoryProduct = inventoryProductMapper.findLatestInventoryByProductId(productId);
        assertThat(actualInventoryProduct).isEmpty();
    }

}
