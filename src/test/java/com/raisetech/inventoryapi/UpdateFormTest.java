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

class UpdateFormTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        Locale.setDefault(Locale.JAPAN);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nameがnullのときにバリデーションエラーとなること() {
        UpdateForm updateForm = new UpdateForm(null);
        Set<ConstraintViolation<UpdateForm>> violations = validator.validate(updateForm);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "空白は許可されていません"));
    }

    @Test
    public void nameが空文字のときにバリデーションエラーとなること() {
        UpdateForm updateForm = new UpdateForm("");
        Set<ConstraintViolation<UpdateForm>> violations = validator.validate(updateForm);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "空白は許可されていません"));
    }

    @Test
    public void nameが31文字以上のときにバリデーションエラーとなること() {
        String name = "Shaft";
        UpdateForm updateForm = new UpdateForm(name.repeat(6) + "1");
        Set<ConstraintViolation<UpdateForm>> violations = validator.validate(updateForm);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(tuple("name", "0 から 30 の間のサイズにしてください"));
    }

    @Test
    public void nameが30文字のときバリデーションエラーとならないこと() {
        String name = "Shaft";
        UpdateForm updateForm = new UpdateForm(name.repeat(6));
        Set<ConstraintViolation<UpdateForm>> violations = validator.validate(updateForm);
        assertThat(violations).isEmpty();
    }

    @Test
    public void nameに値があるときバリデーションエラーとならないこと() {
        UpdateForm updateForm = new UpdateForm("Shaft");
        Set<ConstraintViolation<UpdateForm>> violations = validator.validate(updateForm);
        assertThat(violations).isEmpty();
    }
}
