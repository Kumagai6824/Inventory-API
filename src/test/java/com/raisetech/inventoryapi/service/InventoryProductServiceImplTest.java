package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.Inventory;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InvalidInputException;
import com.raisetech.inventoryapi.exception.InventoryNotLatestException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                .isInstanceOf(InventoryNotLatestException.class)
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

    @Test
    public void 出庫修正後に出庫数が更新されること() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int id = 1;
        int lastUpdatedQuantity = 500;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(id, productId, lastUpdatedQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int inventoryQuantity = 800;
        doReturn(inventoryQuantity).when(inventoryProductMapper).getQuantityByProductId(productId);

        int updatingQuantity = 100;
        inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, updatingQuantity);

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(inventoryProductMapper, times(1)).updateInventoryProductById(idCaptor.capture(), quantityCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(quantityCaptor.getValue()).isEqualTo(updatingQuantity * (-1));
    }

    @Test
    public void 存在しない商品IDで出庫修正時に例外を返すこと() throws Exception {
        int productId = 0;
        int id = 1;
        int quantity = 100;

        doReturn(Optional.empty()).when(productMapper).findById(productId);

        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");
    }

    @Test
    public void 論理削除済み商品IDで出庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        int id = 1;
        int quantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new Product(productId, "test", offsetDateTime))).when(productMapper).findById(productId);

        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID:" + productId + " does not exist");
    }

    @Test
    public void 指定した商品IDの在庫情報が未登録の状態で出庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        doReturn(Optional.empty()).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int id = 1;
        int quantity = 100;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, quantity))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inventory item does not exist");
    }

    @Test
    public void 最新ではない在庫IDで出庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int latestId = 2;
        int inventoryQuantity = 500;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(latestId, productId, inventoryQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int requestId = 1;
        int requestQuantity = 100;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, requestId, requestQuantity))
                .isInstanceOf(InventoryNotLatestException.class)
                .hasMessage("Cannot update id: " + requestId + ", Only the last update can be altered.");
    }

    @Test
    public void 数量ゼロ個で出庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int id = 1;
        int inventoryQuantity = 100;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(id, productId, inventoryQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);


        int requestQuantity = 0;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, requestQuantity))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Quantity must be greater than zero");
    }

    @Test
    public void 在庫数より多い数量で出庫修正時に例外を返すこと() throws Exception {
        int productId = 1;
        doReturn(Optional.of(new Product(productId, "test", null))).when(productMapper).findById(productId);

        int id = 1;
        int lastUpdatedQuantity = 400;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-06-24T10:10:01+09:00");
        doReturn(Optional.of(new InventoryProduct(id, productId, lastUpdatedQuantity, offsetDateTime))).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        int inventoryQuantity = 500;
        doReturn(inventoryQuantity).when(inventoryProductMapper).getQuantityByProductId(productId);

        int requestQuantity = inventoryQuantity - lastUpdatedQuantity + 10;
        assertThatThrownBy(() -> inventoryProductServiceImpl.updateShippedInventoryProductById(productId, id, requestQuantity))
                .isInstanceOf(InventoryShortageException.class)
                .hasMessage("Inventory shortage, only " + (inventoryQuantity - lastUpdatedQuantity) + " items left");

    }

    @Test
    public void 指定した在庫IDで在庫情報を取得できること() {
        int id = 1;
        InventoryProduct expect = new InventoryProduct(id, 1, 100, OffsetDateTime.parse("2024-06-24T10:10:01+09:00"));

        doReturn(Optional.of(expect)).when(inventoryProductMapper).findInventoryById(id);

        InventoryProduct actual = inventoryProductServiceImpl.findInventoryById(id);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    public void 存在しない在庫IDで在庫取得時に例外を返すこと() {
        int id = 0;
        doReturn(Optional.empty()).when(inventoryProductMapper).findInventoryById(id);

        assertThatThrownBy(() -> inventoryProductServiceImpl.findInventoryById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("ID:" + id + " does not exist");
    }

    @Test
    public void 指定した在庫IDで在庫情報を削除できること() {
        int id = 1;
        int productId = 1;
        Optional<InventoryProduct> inventoryProduct = Optional.of(new InventoryProduct(id, productId, 100, OffsetDateTime.parse("2024-06-24T10:10:01+09:00")));

        doReturn(inventoryProduct).when(inventoryProductMapper).findInventoryById(id);
        doReturn(inventoryProduct).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        inventoryProductServiceImpl.deleteInventoryById(id);

        verify(inventoryProductMapper, times(1)).deleteInventoryById(id);

    }

    @Test
    public void 存在しない在庫IDで在庫削除時に例外を返すこと() {
        int id = 0;
        InventoryProduct inventoryProduct = new InventoryProduct(id, 1, 100, OffsetDateTime.parse("2024-06-24T10:10:01+09:00"));

        doReturn(Optional.empty()).when(inventoryProductMapper).findInventoryById(id);

        assertThatThrownBy(() -> inventoryProductServiceImpl.deleteInventoryById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("ID:" + id + " does not exist");
    }

    @Test
    public void 最新ではない在庫IDで在庫削除時に例外を返すこと() {
        int requestId = 1;
        int productId = 1;
        Optional<InventoryProduct> inventoryProduct = Optional.of(new InventoryProduct(requestId, productId, 100, OffsetDateTime.parse("2024-06-24T10:10:01+09:00")));
        doReturn(inventoryProduct).when(inventoryProductMapper).findInventoryById(requestId);

        int latestId = 2;
        Optional<InventoryProduct> latestInventoryProduct = Optional.of(new InventoryProduct(latestId, productId, 100, OffsetDateTime.parse("2024-06-24T10:10:01+09:00")));
        doReturn(latestInventoryProduct).when(inventoryProductMapper).findLatestInventoryByProductId(productId);

        assertThatThrownBy(() -> inventoryProductServiceImpl.deleteInventoryById(requestId))
                .isInstanceOf(InventoryNotLatestException.class)
                .hasMessage("Cannot update id: " + requestId + ", Only the last update can be altered.");
    }

    @Test
    public void 現在庫数を取得できること() {
        List<Product> products = new ArrayList<Product>(Arrays.asList(
                new Product(1, "test", null),
                new Product(2, "test2", null)));

        doReturn(products).when(productMapper).findAll();
        doReturn(100).when(inventoryProductMapper).getQuantityByProductId(1);
        doReturn(500).when(inventoryProductMapper).getQuantityByProductId(2);

        List<Inventory> expectedCurrentInventories = new ArrayList<Inventory>(Arrays.asList(
                new Inventory(1, "test", 100),
                new Inventory(2, "test2", 500)
        ));

        List<Inventory> actualCurrentInventories = inventoryProductServiceImpl.getCurrentInventories();

        assertThat(actualCurrentInventories).isEqualTo(expectedCurrentInventories);
    }

    @Test
    public void 現在庫数を取得時に在庫が存在しないとき数量0のレコードを返すこと() {
        List<Product> products = new ArrayList<Product>(Arrays.asList(
                new Product(1, "test", null),
                new Product(2, "test2", null)));

        doReturn(products).when(productMapper).findAll();
        doReturn(0).when(inventoryProductMapper).getQuantityByProductId(1);
        doReturn(0).when(inventoryProductMapper).getQuantityByProductId(2);

        List<Inventory> expectedCurrentInventories = new ArrayList<Inventory>(Arrays.asList(
                new Inventory(1, "test", 0),
                new Inventory(2, "test2", 0)
        ));

        List<Inventory> actualCurrentInventories = inventoryProductServiceImpl.getCurrentInventories();

        assertThat(actualCurrentInventories).isEqualTo(expectedCurrentInventories);
    }

    @Test
    public void 現在庫数を取得時に商品が存在しないとき空を返すこと() {
        doReturn(List.of()).when(productMapper).findAll();

        List<Inventory> actualCurrentInventories = inventoryProductServiceImpl.getCurrentInventories();

        assertThat(actualCurrentInventories).isEqualTo(List.of());
    }
}
