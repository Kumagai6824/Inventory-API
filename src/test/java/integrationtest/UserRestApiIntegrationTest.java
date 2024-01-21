package integrationtest;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.raisetech.inventoryapi.Work09Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Work09Application.class)
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRestApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DataSet(value = "products.yml")
    @Transactional
    void 商品情報が全件取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                         [
                            {
                               "id":1,
                               "name":"Bolt 1"
                            },
                            {
                               "id":2,
                               "name":"Washer"
                            }
                         ]           
                        """
                , response, JSONCompareMode.STRICT);

    }

    @Test
    @DataSet(value = "products.yml")
    @Transactional
    void 指定したIDの商品情報を取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                        {
                           "id":1,
                           "name":"Bolt 1"
                        }
                        """
                , response, JSONCompareMode.STRICT);

    }
    
    @DataSet(value = "products.yml")
    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void 存在しないIDを指定した際期待通り404を返すこと(int productId) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("404", JsonPath.read(response, "$.status"));
        assertEquals("/products/" + productId, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("Product ID:" + productId + " does not exist", JsonPath.read(response, "$.message"));
    }
}
