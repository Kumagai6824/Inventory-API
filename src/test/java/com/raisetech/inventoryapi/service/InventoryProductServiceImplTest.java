package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryProductServiceImplTest {
    @InjectMocks
    InventoryProductServiceImpl inventoryProductServiceImpl;
    @Mock
    InventoryProductMapper inventoryProductMapper;
    @Mock
    ProductMapper productMapper;

    @Test
    public void 在庫入庫処理時に在庫が登録されること() throws Exception {
        int productId = 1;
        Optional<Product> product = Optional.of(new Product(productId, "test", null));

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(product).when(productMapper).findById(productId);
        doNothing().when(inventoryProductMapper).createInventoryProduct(inventoryProduct);
        inventoryProductServiceImpl.receivingInventoryProduct(inventoryProduct);
        verify(inventoryProductMapper, times(1)).createInventoryProduct(inventoryProduct);

    }

    @Test
    public void 存在しない商品IDで在庫登録時に例外をスローすること() throws Exception {
        int productId = 0;

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(Optional.empty()).when(productMapper).findById(productId);
        assertThatThrownBy(() -> inventoryProductServiceImpl.receivingInventoryProduct(inventoryProduct))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");

    }

    @Test
    public void 論理削除済み商品IDで在庫登録時に例外をスローすること() throws Exception {
        int productId = 1;
        OffsetDateTime deletedDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        Optional<Product> product = Optional.of(new Product(productId, "test", deletedDateTime));

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(product).when(productMapper).findById(productId);
        assertThatThrownBy(() -> inventoryProductServiceImpl.receivingInventoryProduct(inventoryProduct))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");

    }

    @Test
    public void 数量ゼロ個で在庫登録時に例外をスローすること() throws Exception {
        int productId = 1;
        int quantity = 0;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        assertThatThrownBy(() -> inventoryProductServiceImpl.receivingInventoryProduct(inventoryProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be greater than zero");
    }
}
