package com.raisetech.inventoryapi;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

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
                        new Product(1, "Bolt 1"),
                        new Product(2, "Washer")
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
        assertThat(product).contains(new Product(1, "Bolt 1"));
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
    void 登録処理が完了して引数のユーザーと新しく採番されたIDが設定されること() {
        Product product = new Product();
        product.setName("Gear1");
        productMapper.createProduct(product);
        assertNotNull(product.getId());
        assertThat(productMapper.findById(1)).contains(new Product(1, "Gear1"));
    }

}
