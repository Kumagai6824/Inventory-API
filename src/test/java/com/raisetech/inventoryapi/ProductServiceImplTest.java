package com.raisetech.inventoryapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    ProductServiceImpl productServiceImpl;
    @Mock
    ProductMapper productMapper;

    @Test
    public void 製品情報を全件取得できること() {
        List<Product> productList = List.of(new Product("Bolt 1"), new Product("Washer"));

        doReturn(productList).when(productMapper).findAll();
        List<Product> actual = productServiceImpl.findAll();
        assertThat(actual).isEqualTo(productList);
    }

    @Test
    public void 存在する商品IDを指定したときに正常に商品情報が返されること() throws Exception {
        doReturn(Optional.of(new Product("Bolt 1"))).when(productMapper).findById(1);

        Product actual = productServiceImpl.findById(1);
        assertThat(actual).isEqualTo(new Product("Bolt 1"));
    }

    @Test
    public void 存在しない商品IDを指定したときに期待通り例外を返すこと() {
        doReturn(Optional.empty()).when(productMapper).findById(0);
        assertThatThrownBy(() -> productServiceImpl.findById(0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + 0 + " does not exist");
    }

    @Test
    public void 商品が正しく1件登録されること() {
        Product product = new Product();
        doNothing().when(productMapper).createProduct(product);
        productServiceImpl.createProduct(product);
        verify(productMapper, times(1)).createProduct(product);

    }
}
