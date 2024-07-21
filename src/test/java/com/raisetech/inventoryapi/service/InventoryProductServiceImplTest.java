package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InvalidInputException;
import com.raisetech.inventoryapi.exception.InventoryShortageException;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Quantity must be greater than zero");
    }

    @Test
    public void 在庫出庫処理時に在庫が登録されること() throws Exception {
        int productId = 1;
        Optional<Product> product = Optional.of(new Product(productId, "test", null));

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(product).when(productMapper).findById(productId);
        doNothing().when(inventoryProductMapper).createInventoryProduct(inventoryProduct);
        doReturn(quantity).when(inventoryProductMapper).getQuantityByProductId(productId);
        inventoryProductServiceImpl.shippingInventoryProduct(inventoryProduct);

        ArgumentCaptor<InventoryProduct> argument = ArgumentCaptor.forClass(InventoryProduct.class);
        verify(inventoryProductMapper, times(1)).createInventoryProduct(argument.capture());
        Assertions.assertEquals(quantity * (-1), argument.getValue().getQuantity());

    }

    @Test
    public void 存在しない商品IDで出庫時に例外をスローすること() throws Exception {
        int productId = 0;

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(Optional.empty()).when(productMapper).findById(productId);
        assertThatThrownBy(() -> inventoryProductServiceImpl.shippingInventoryProduct(inventoryProduct))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");

    }

    @Test
    public void 論理削除済み商品IDで出庫時に例外をスローすること() throws Exception {
        int productId = 1;
        OffsetDateTime deletedDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        Optional<Product> product = Optional.of(new Product(productId, "test", deletedDateTime));

        int quantity = 1000;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        doReturn(product).when(productMapper).findById(productId);
        assertThatThrownBy(() -> inventoryProductServiceImpl.shippingInventoryProduct(inventoryProduct))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");

    }

    @Test
    public void 数量ゼロ個で出庫時に例外をスローすること() throws Exception {
        int productId = 1;
        Optional<Product> product = Optional.of(new Product(productId, "test", null));
        doReturn(product).when(productMapper).findById(productId);

        int quantity = 0;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(quantity);

        assertThatThrownBy(() -> inventoryProductServiceImpl.shippingInventoryProduct(inventoryProduct))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Quantity must be greater than zero");
    }

    @Test
    public void 在庫数より多い数量で出庫時に例外をスローすること() throws Exception {
        int productId = 1;
        Optional<Product> product = Optional.of(new Product(productId, "test", null));
        doReturn(product).when(productMapper).findById(productId);

        int inventoryQuantity = 500;
        doReturn(inventoryQuantity).when(inventoryProductMapper).getQuantityByProductId(productId);

        int shippingQuantity = inventoryQuantity + 1;
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setProductId(productId);
        inventoryProduct.setQuantity(shippingQuantity);

        assertThatThrownBy(() -> inventoryProductServiceImpl.shippingInventoryProduct(inventoryProduct))
                .isInstanceOf(InventoryShortageException.class)
                .hasMessage("Inventory shortage, only " + inventoryQuantity + " items left");
    }

    @Test
    public void 入庫修正後に入庫数が更新されること() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int id = 1;
        int inventoryQuantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(id, productId, inventoryQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int updatingQuantity = 250;
        inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, id, updatingQuantity);

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(inventoryProductMapper, times(1)).updateInventoryProductById(idCaptor.capture(), quantityCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(quantityCaptor.getValue()).isEqualTo(updatingQuantity);
    }

    @Test
    public void 存在しない商品IDで入庫修正時に例外を返すこと() throws Exception {
        int productId = 0;
        int id = 1;
        int quantity = 6000;

        doReturn(Optional.empty()).when(productMapper).findById(productId);

        assertThatThrownBy(() -> inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");
    }

    @Test
    public void 論理削除済み商品IDで入庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        int id = 1;
        int quantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new Product(productId, "test", offsetDateTime))).when(productMapper).findById(productId);

        assertThatThrownBy(() -> inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");
    }

    @Test
    public void 指定した商品IDの在庫情報が未登録の状態で入庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        doReturn(Optional.empty()).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int id = 1;
        int quantity = 100;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inventory item does not exist");
    }

    @Test
    public void 最新ではない在庫IDで入庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int latestId = 2;
        int inventoryQuantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(latestId, productId, inventoryQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int requestId = 1;
        int requestQuantity = 250;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, requestId, requestQuantity))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Cannot update id: " + requestId + ", Only the last update can be altered.");
    }

    @Test
    public void 数量ゼロ個で入庫修正時に例外をスローすること() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int id = 1;
        int inventoryQuantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(id, productId, inventoryQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);


        int requestQuantity = 0;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateReceivedInventoryProductById(productId, id, requestQuantity))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Quantity must be greater than zero");
    }
}
