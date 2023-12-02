package com.raisetech.inventoryapi;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//Replace.NONE：テスト用データベースの設定を手動で行う
class InventoryMapperTest {

    @Autowired
    InventoryMapper inventoryMapper;

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql", "classpath:/insert-names.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void すべてのユーザーが取得できること() {
        List<Inventory> inventories = inventoryMapper.findAll();
        assertThat(inventories)
                .hasSize(3)
                .contains(
                        new Inventory(1, "Shimizu"),
                        new Inventory(2, "Koyama"),
                        new Inventory(3, "Tanaka")
                );
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void レコードが存在しないときに取得されるListが空であること() {
        List<Inventory> inventories = inventoryMapper.findAll();
        assertThat(inventories).isEmpty();
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql", "classpath:/reset-id.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 登録処理が完了して引数のユーザーと新しく採番されたIDが設定されること() {
        Inventory inventory = new Inventory();
        inventory.setName("Kumagai");
        inventoryMapper.createName(inventory);
        assertNotNull(inventory.getId());
        assertThat(inventoryMapper.findById(1)).contains(new Inventory(1, "Kumagai"));
    }

}
