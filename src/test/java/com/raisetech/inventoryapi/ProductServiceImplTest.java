package com.raisetech.inventoryapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    ProductServiceImpl productServiceImpl;
    @Mock
    ProductMapper productMapper;

    @Test
    public void 存在するユーザーIDを指定したときに正常にユーザーが返されること() throws Exception {
        doReturn(Optional.of(new Product("koyama"))).when(productMapper).findById(1);
        //findById(1)実行時、必ずid:1, name:yoshihito koyama　を返す

        Product actual = productServiceImpl.findById(1);
        assertThat(actual).isEqualTo(new Product("koyama"));
    }

    @Test
    public void 存在しないユーザーIDを指定したときに期待通り例外を返すこと() {
        doReturn(Optional.empty()).when(productMapper).findById(0);
        assertThatThrownBy(() -> productServiceImpl.findById(0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("resource not found with id: 0");
    }
}
