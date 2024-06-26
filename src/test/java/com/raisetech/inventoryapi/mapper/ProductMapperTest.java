package com.raisetech.inventoryapi.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.raisetech.inventoryapi.entity.InventoryHistory;
import com.raisetech.inventoryapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBRider
@DataSet(value = {"products.yml", "inventoryProducts.yml"}, executeScriptsBefore = {"reset-id.sql", "reset-inventoryProductId.sql"}, cleanAfter = true, transactional = true)
class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @BeforeEach
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    }

    @Test
    @Transactional
    void すべての製品情報が取得できること() {
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(4)
                .contains(
                        new Product(1, "Bolt 1", null),
                        new Product(2, "Washer", null),
                        new Product(3, "Gear", null),
                        new Product(4, "Shaft", null)
                );
    }

    @Test
    @DataSet(value = "empty-products.yml")
    @Transactional
    void レコードが存在しないときに取得されるListが空であること() {
        List<Product> products = productMapper.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @Transactional
    void 指定した商品IDのデータを返すこと() {
        Optional<Product> product = productMapper.findById(1);
        assertThat(product).contains(new Product(1, "Bolt 1", null));
    }

    @Test
    @Transactional
    void 存在しない商品IDを指定したときに空で返すこと() {
        Optional<Product> product = productMapper.findById(0);
        assertThat(product).isEmpty();
        Optional<Product> product2 = productMapper.findById(10);
        assertThat(product2).isEmpty();
    }


    @Test
    @Transactional
    void 登録処理が完了して商品情報と新しく採番されたIDが設定されること() {
        Product product = new Product();
        product.setName("Gear1");
        productMapper.createProduct(product);

        int productId = product.getId();
        assertNotNull(productId);
        assertThat(productMapper.findById(productId)).contains(new Product(productId, "Gear1", null));
    }

    @Test
    @Transactional
    void 更新処理が完了して正しく商品情報が設定されること() {
        productMapper.updateProductById(1, "Shaft");
        assertThat(productMapper.findById(1)).contains(new Product(1, "Shaft", null));
    }

    @Test
    @Transactional
    void 削除済みレコードを更新しても処理されないこと() {
        int productId = 1;
        String presentName = productMapper.findById(productId).get().getName();
        productMapper.deleteProductById(productId);
        productMapper.updateProductById(productId, "updatedName");
        String nameAfterUpdate = productMapper.findById(productId).get().getName();

        assertEquals(presentName, nameAfterUpdate);

    }

    @Test
    @Transactional
    void 削除後に論理削除されたレコードが取得されないこと() {
        productMapper.deleteProductById(1);
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(3)
                .contains(
                        new Product(2, "Washer", null),
                        new Product(3, "Gear", null),
                        new Product(4, "Shaft", null)
                );
    }

    @Test
    @Transactional
    void 存在しない商品IDで削除してもレコードに影響がないこと() {
        productMapper.deleteProductById(0);
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(4)
                .contains(
                        new Product(1, "Bolt 1", null),
                        new Product(2, "Washer", null),
                        new Product(3, "Gear", null),
                        new Product(4, "Shaft", null)
                );
    }


    @Test
    @Transactional
    void 指定した商品IDの在庫履歴が取得できること() {
        List<InventoryHistory> inventoryHistory = productMapper.findHistoriesByProductId(1);
        OffsetDateTime expectedDateTime = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        assertThat(inventoryHistory)
                .hasSize(1)
                .contains(
                        new InventoryHistory(1, 1, "Bolt 1", 100, expectedDateTime)
                );
    }

    @Test
    @Transactional
    void 指定した商品IDの在庫がゼロ個の場合在庫履歴を取得できること() {
        List<InventoryHistory> inventoryHistory = productMapper.findHistoriesByProductId(3);
        OffsetDateTime expectedDateTime = OffsetDateTime.parse("2024-05-10T12:58:10+09:00");
        OffsetDateTime expectedDateTime2 = OffsetDateTime.parse("2024-05-11T12:58:10+09:00");
        assertThat(inventoryHistory)
                .hasSize(2)
                .contains(
                        new InventoryHistory(3, 3, "Gear", 500, expectedDateTime),
                        new InventoryHistory(4, 3, "Gear", -500, expectedDateTime2)
                );
    }

    @Test
    @Transactional
    void 指定した商品IDの在庫が未登録の場合空を返すこと() {
        List<InventoryHistory> inventoryHistory = productMapper.findHistoriesByProductId(4);
        assertThat(inventoryHistory).isEmpty();
    }

    @Test
    @Transactional
    void 存在しない商品IDで在庫履歴取得時空を返すこと() {
        List<InventoryHistory> inventoryHistory = productMapper.findHistoriesByProductId(0);
        assertThat(inventoryHistory).isEmpty();
    }

    @Test
    @Transactional
    void 論理削除した商品IDで在庫履歴が取得できること() {
        productMapper.deleteProductById(1);
        List<InventoryHistory> inventoryHistory = productMapper.findHistoriesByProductId(1);
        OffsetDateTime expectedDateTime = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        assertThat(inventoryHistory)
                .hasSize(1)
                .contains(new InventoryHistory(1, 1, "Bolt 1", 100, expectedDateTime));
    }

}
