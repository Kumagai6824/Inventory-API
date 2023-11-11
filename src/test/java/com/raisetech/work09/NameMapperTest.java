package com.raisetech.work09;

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
class NameMapperTest {

    @Autowired
    NameMapper nameMapper;

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql", "classpath:/insert-names.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void すべてのユーザーが取得できること() {
        List<Name> names = nameMapper.findAll();
        assertThat(names)
                .hasSize(3)
                .contains(
                        new Name(1, "Shimizu"),
                        new Name(2, "Koyama"),
                        new Name(3, "Tanaka")
                );
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void レコードが存在しないときに取得されるListが空であること() {
        List<Name> names = nameMapper.findAll();
        assertThat(names).isEmpty();
    }

    @Test
    @Sql(
            scripts = {"classpath:/delete-names.sql", "classpath:/reset-id.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Transactional
    void 登録処理が完了して引数のユーザーと新しく採番されたIDが設定されること() {
        Name name = new Name();
        name.setName("Kumagai");
        nameMapper.createName(name);
        assertNotNull(name.getId());
        assertThat(nameMapper.findById(1)).contains(new Name(1, "Kumagai"));
    }

}
