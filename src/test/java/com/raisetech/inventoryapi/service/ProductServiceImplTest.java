package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryHistory;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
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
    @Mock
    InventoryProductMapper inventoryProductMapper;

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
    public void 削除した商品IDを指定したときに例外を返すこと() {
        int id = 1;
        int quantity = 0;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setQuantity(quantity);
        when(inventoryProductMapper.getQuantityByProductId(id)).thenReturn(quantity);

        when(productMapper.findById(id)).thenReturn(Optional.of(new Product()));

        productServiceImpl.deleteProductById(id);
        verify(productMapper).findById(id);

        when(productMapper.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productServiceImpl.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + id + " does not exist");
    }

    @Test
    public void 商品が正しく1件登録されること() throws Exception {
        Product product = new Product("test product");
        doNothing().when(productMapper).createProduct(product);
        productServiceImpl.createProduct(product);
        verify(productMapper, times(1)).createProduct(product);

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    public void 商品IDを指定して更新したときに商品情報が更新されること(int id) throws Exception {
        String initialName = "Bolt";
        String renewedName = "Shaft";
        when(productMapper.findById(id)).thenReturn(Optional.of(new Product(id, initialName, null)));
        productServiceImpl.updateProductById(id, renewedName);
        verify(productMapper, times(1)).updateProductById(id, renewedName);
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    public void 存在しない商品IDを指定して更新したときに期待通り例外を返すこと(int id) throws Exception {
        String renewedName = "Shaft";
        when(productMapper.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productServiceImpl.updateProductById(id, renewedName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("resource not found with id: " + id);
    }

    @Test
    public void 削除済み商品を更新したときに例外を返すこと() throws Exception {
        int id = 1;
        String renewedName = "Shaft";
        when(productMapper.findById(id)).thenReturn(Optional.of(new Product()));

        productServiceImpl.deleteProductById(id);
        verify(productMapper).deleteProductById(id);

        when(productMapper.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productServiceImpl.updateProductById(id, renewedName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("resource not found with id: " + id);
    }

    @Test
    public void 商品を削除した際在庫が0個なら削除されること() throws Exception {
        int productId = 1;
        Product product = new Product(productId, "test", null);

        int quantity = 0;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setQuantity(quantity);

        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryProductMapper.getQuantityByProductId(productId)).thenReturn(quantity);

        productServiceImpl.deleteProductById(productId);

        verify(productMapper, times(1)).deleteProductById(productId);
    }

    @Test
    public void 存在しない商品IDを指定して削除した場合に例外を返すこと() {
        int id = 0;
        doReturn(Optional.empty()).when(productMapper).findById(id);
        assertThatThrownBy(() -> productServiceImpl.deleteProductById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("resource not found with id: " + id);
    }

    @Test
    public void 指定した商品IDの在庫履歴を取得できること() {
        int inventoryId = 1;
        int productId = 1;
        OffsetDateTime dateTime = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        List<InventoryHistory> history = List.of(new InventoryHistory(inventoryId, productId, "Test", 100, dateTime));

        doReturn(Optional.of(new Product("Test"))).when(productMapper).findById(productId);
        doReturn(history).when(productMapper).findHistoriesByProductId(productId);
        List<InventoryHistory> actual = productServiceImpl.findHistoriesByProductId(productId);
        assertThat(actual).isEqualTo(history);
    }

    @Test
    public void 在庫がゼロの時指定した商品IDの在庫履歴を取得できること() {
        int inventoryId = 1;
        int productId = 1;
        OffsetDateTime dateTime = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        List<InventoryHistory> history = List.of(new InventoryHistory(inventoryId, productId, "Test", 0, dateTime));

        doReturn(Optional.of(new Product("Test"))).when(productMapper).findById(productId);
        doReturn(history).when(productMapper).findHistoriesByProductId(productId);
        List<InventoryHistory> actual = productServiceImpl.findHistoriesByProductId(productId);
        assertThat(actual).isEqualTo(history);
    }

    @Test
    public void 存在しない商品IDで在庫履歴取得時例外を返すこと() {
        int id = 0;
        doReturn(Optional.empty()).when(productMapper).findById(id);
        assertThatThrownBy(() -> productServiceImpl.findHistoriesByProductId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("resource not found with id: " + id);
    }

    @Test
    public void 削除済み商品IDで在庫履歴が取得できること() {
        int inventoryId = 1;
        int productId = 1;

        Product existingProduct = new Product(productId, "test", null);

        when(productMapper.findById(productId)).thenReturn(Optional.of(existingProduct));

        productServiceImpl.deleteProductById(productId);
        verify(productMapper).deleteProductById(productId);

        when(productMapper.findById(productId)).thenReturn(Optional.of(existingProduct));

        OffsetDateTime dateTime = OffsetDateTime.parse("2023-12-10T23:58:10+09:00");
        List<InventoryHistory> history = List.of(new InventoryHistory(inventoryId, productId, "Test", 100, dateTime));

        when(productMapper.findHistoriesByProductId(productId)).thenReturn(history);

        List<InventoryHistory> actual = productServiceImpl.findHistoriesByProductId(productId);
        assertThat(actual).isEqualTo(history);
    }
}
