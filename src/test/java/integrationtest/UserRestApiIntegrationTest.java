package integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.raisetech.inventoryapi.Product;
import com.raisetech.inventoryapi.Work09Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

    @Transactional
    @DataSet(value = "products.yml")
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

    @Test
    @DataSet(value = "products.yml")
    @Transactional
    void 新規商品を登録でき201を返すこと() throws Exception {
        Product request = new Product("Shaft");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString((StandardCharsets.UTF_8));

        JSONAssert.assertEquals("""
                        {
                           "message":"name:Shaft was successfully created"
                        }
                        """
                , response, JSONCompareMode.STRICT);
    }

    @Test
    @DataSet(value = "products.yml")
    @Transactional
    void 新規登録後DBにレコードが登録されていること() throws Exception {
        Product request = new Product("Shaft");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String locationHeader = postResult.getResponse().getHeader("Location");
        String[] locationParts = locationHeader.split("/");
        int id = Integer.parseInt(locationParts[locationParts.length - 1]);
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(id, (Integer) JsonPath.read(response, "$.id"));
        assertEquals("Shaft", JsonPath.read(response, "$.name"));


    }

    @Transactional
    @DataSet(value = "products.yml")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234567890123456789012345678901"})
    void 新規登録時nullまたは空文字または31文字以上のリクエストの場合400を返すこと(String str) throws Exception {
        Product request = new Product(str);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String responseActual = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String responseNullCaseA = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"name",
                         "message":"must not be blank"
                      },
                      {
                         "field":"name",
                         "message":"must not be null"
                      }
                   ]
                }
                """;
        String responseNullCaseB = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"name",
                         "message":"must not be null"
                      },
                      {
                         "field":"name",
                         "message":"must not be blank"
                      }
                   ]
                }
                """;
        String responseEmpltyCase = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"name",
                         "message":"must not be blank"
                      }
                   ]
                }
                """;
        String responseLengthCase = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"name",
                         "message":"size must be between 0 and 30"
                      }
                   ]
                }
                """;

        if (str == null) {
            try {
                JSONAssert.assertEquals(responseNullCaseA, responseActual, JSONCompareMode.STRICT);
            } catch (AssertionError e) {
                JSONAssert.assertEquals(responseNullCaseB, responseActual, JSONCompareMode.STRICT);
            }
        } else if (str == "") {
            JSONAssert.assertEquals(responseEmpltyCase, responseActual, JSONCompareMode.STRICT);
        } else JSONAssert.assertEquals(responseLengthCase, responseActual, JSONCompareMode.STRICT);
    }
}
