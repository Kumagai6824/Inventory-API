package com.raisetech.inventoryapi;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CreateFormTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        Locale.setDefault(Locale.JAPAN);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nameがnullのときにバリデーションエラーとなること() {
        CreateForm createForm = new CreateForm(null);
        Set<ConstraintViolation<CreateForm>> violations = validator.validate(createForm);
        assertThat(violations).hasSize(2);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "空白は許可されていません"), tuple("name", "null は許可されていません"));
    }

    @Test
    public void nameが空文字のときにバリデーションエラーとなること() {
        CreateForm createForm = new CreateForm("");
        Set<ConstraintViolation<CreateForm>> violations = validator.validate(createForm);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "空白は許可されていません"));
    }

    @Test
    public void nameが31文字以上のときにバリデーションエラーとなること() {
        CreateForm createForm = new CreateForm("1234567890123456789012345678901");
        Set<ConstraintViolation<CreateForm>> violations = validator.validate(createForm);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "0 から 30 の間のサイズにしてください"));
    }

    @Test
    public void nameに値があるときバリデーションエラーとならないこと() {
        CreateForm createForm = new CreateForm("Shaft");
        Set<ConstraintViolation<CreateForm>> violations = validator.validate(createForm);
        assertThat(violations).isEmpty();
    }
}
