package com.raisetech.inventoryapi.mapper;

import com.raisetech.inventoryapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @BeforeEach
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void すべての製品情報が取得できること() {
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(2)
                .contains(
                        new Product(1, "Bolt 1", null),
                        new Product(2, "Washer", null)
                );
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void レコードが存在しないときに取得されるListが空であること() {
        List<Product> products = productMapper.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 指定した商品IDのデータを返すこと() {
        Optional<Product> product = productMapper.findById(1);
        assertThat(product).contains(new Product(1, "Bolt 1", null));
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 存在しない商品IDを指定したときに空で返すこと() {
        Optional<Product> product = productMapper.findById(0);
        assertThat(product).isEmpty();
        Optional<Product> product2 = productMapper.findById(10);
        assertThat(product2).isEmpty();
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/reset-id.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 登録処理が完了して商品情報と新しく採番されたIDが設定されること() {
        Product product = new Product();
        product.setName("Gear1");
        productMapper.createProduct(product);
        assertNotNull(product.getId());
        assertThat(productMapper.findById(1)).contains(new Product(1, "Gear1", null));
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 更新処理が完了して正しく商品情報が設定されること() {
        productMapper.updateProductById(1, "Shaft");
        assertThat(productMapper.findById(1)).contains(new Product(1, "Shaft", null));
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 論理削除後にdeletedAtに処理日時が入ること() {
        OffsetDateTime beforeDeletion = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        productMapper.deleteProductById(1);
        OffsetDateTime afterDeletion = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(2)
                .filteredOn(product -> product.getId() == 1)
                .first()
                .satisfies(product -> {
                    assertThat(product.getDeletedAt()).isNotNull();
                    assertThat(product.getDeletedAt()).isBetween(beforeDeletion, afterDeletion);
                });


    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-products.sql", "classpath:/insert-products.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 存在しない商品IDで削除してもレコードに影響がないこと() {
        productMapper.deleteProductById(0);
        List<Product> products = productMapper.findAll();
        assertThat(products)
                .hasSize(2)
                .contains(
                        new Product(1, "Bolt 1", null),
                        new Product(2, "Washer", null)
                );
    }

}
