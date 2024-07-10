package integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.raisetech.inventoryapi.Work09Application;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Work09Application.class)
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataSet(value = {"products.yml", "inventoryProducts.yml"}, executeScriptsBefore = {"reset-id.sql", "reset-inventoryProductId.sql"}, cleanBefore = true, transactional = true)
public class UserRestApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Transactional
    void 商品情報が全件取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                         [
                            {
                               "id":1,
                               "name":"Bolt 1",
                               "deletedAt":null
                            },
                            {
                               "id":2,
                               "name":"Washer",
                               "deletedAt":null
                            },
                            {
                               "id":3,
                               "name":"Gear",
                               "deletedAt":null
                            },
                            {
                               "id":4,
                               "name":"Shaft",
                               "deletedAt":null
                            }
                         ]           
                        """
                , response, JSONCompareMode.STRICT);

    }

    @Test
    @Transactional
    void 指定したIDの商品情報を取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                        {
                           "id":1,
                           "name":"Bolt 1",
                           "deletedAt":null
                        }
                        """
                , response, JSONCompareMode.STRICT);

    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void 存在しないIDを指定した際期待通り404を返すこと(int productId) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + productId, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("Product ID:" + productId + " does not exist", JsonPath.read(response, "$.message"));
    }

    @Test
    @Transactional
    void 削除したIDを指定した際404を返すこと() throws Exception {
        int productId = 3;
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + productId));
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + productId, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("Product ID:" + productId + " does not exist", JsonPath.read(response, "$.message"));
    }

    @Test
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

    @Test
    @ExpectedDataSet(value = {"/dataset/expectedUpdatedProducts.yml"}, ignoreCols = {"id"})
    @Transactional
    void 商品を更新後DBに更新されたレコードがあり200を返すこと() throws Exception {
        int id = 1;
        Product request = new Product("Shaft");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String updateResult = mockMvc.perform(MockMvcRequestBuilders.patch("/products/" + id)
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString((StandardCharsets.UTF_8));

        JSONAssert.assertEquals("""
                        {
                           "message":"Successful operation"
                        }
                        """
                , updateResult, JSONCompareMode.STRICT);
    }

    @Test
    @Transactional
    void 商品更新時存在しないIDを指定した際に404を返すこと() throws Exception {
        Product request = new Product("Shaft");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString((request));
        int productId = 0;
        String response = mockMvc.perform(MockMvcRequestBuilders.patch("/products/" + productId)
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + productId, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("resource not found with id: " + productId, JsonPath.read(response, "$.message"));
    }

    @Test
    @Transactional
    void 削除した商品を更新した際に404を返すこと() throws Exception {
        Product request = new Product("Shaft");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString((request));
        int productId = 3;
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + productId));
        String response = mockMvc.perform(MockMvcRequestBuilders.patch("/products/" + productId)
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + productId, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("resource not found with id: " + productId, JsonPath.read(response, "$.message"));
    }

    @Test
    @DataSet(value = "products.yml")
    @Transactional
    void 商品の削除処理後当該レコードの削除フラグに日付が入り200を返すこと() throws Exception {
        int id = 1;
        String deleteResult = mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                        {
                           "message":"Successful operation"
                        }
                        """
                , deleteResult, JSONCompareMode.STRICT);

        String expectedDeletedProductsJson = """
                {
                  "products": [
                    {
                      "id": 1,
                      "name": "Bolt 1",
                      "deletedAt": "2024-04-22T10:00:00Z"
                    },
                    {
                      "id": 2,
                      "name": "Washer",
                      "deletedAt": null
                    }
                  ]
                }                
                """;

        JSONArray expectedDeletedProductsArray = new JSONObject(expectedDeletedProductsJson)
                .getJSONArray("products");

        for (int i = 0; i < expectedDeletedProductsArray.length(); i++) {
            JSONObject expectedProduct = expectedDeletedProductsArray.getJSONObject(i);
            int expectedProductId = expectedProduct.getInt("id");
            if (expectedProductId == id) {
                OffsetDateTime expectedDeletedAt = expectedProduct.isNull("deletedAt") ? null :
                        OffsetDateTime.parse(expectedProduct.getString("deletedAt"),
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                assertThat(expectedDeletedAt).isNotNull();
            }
        }
    }

    @Test
    @Transactional
    void 商品削除時存在しないIDを指定した際に404を返すこと() throws Exception {
        int id = 0;
        String response = mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + id, JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("resource not found with id: " + id, JsonPath.read(response, "$.message"));
    }

    @Test
    @Transactional
    void 指定した商品IDの在庫履歴を全件取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/1/histories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                         [
                            {
                               "id":1,
                               "productId": 1,
                               "name": "Bolt 1",
                               "quantity": 100,
                               "history": "2023-12-10T23:58:10+09:00"
                            }
                         ]
                        """
                , response, JSONCompareMode.STRICT);

    }

    @Test
    @Transactional
    void 削除済み商品IDの在庫履歴を全件取得できること() throws Exception {
        int productId = 3;
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId + "/histories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                         [
                            {
                               "id": 3,
                               "productId": %s,
                               "name": "Gear",
                               "quantity": 500,
                               "history": "2024-05-10T12:58:10+09:00"
                            },
                            {
                               "id": 4,
                               "productId": %s,
                               "name": "Gear",
                               "quantity": -500,
                               "history": "2024-05-11T12:58:10+09:00"
                            }
                         ]
                        """.formatted(productId, productId)
                , response, JSONCompareMode.STRICT);

    }

    @Test
    @Transactional
    void 存在しない商品IDで在庫履歴取得時404を返すこと() throws Exception {
        int productId = 0;
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId + "/histories"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/products/" + productId + "/histories", JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("resource not found with id: " + productId, JsonPath.read(response, "$.message"));
    }

    @Test
    @ExpectedDataSet(value = "/dataset/expectedReceivedInventoryProducts.yml", ignoreCols = "history")
    @Transactional
    void 入庫処理ができ201を返しレコードが登録されていること() throws Exception {
        InventoryProduct request = new InventoryProduct();
        request.setProductId(1);
        request.setQuantity(1000);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/received-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString((StandardCharsets.UTF_8));

        JSONAssert.assertEquals("""
                        {
                           "message":"item was successfully received"
                        }
                        """
                , response, JSONCompareMode.STRICT);
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {0, -500})
    void 入庫処理時の数量が0以下の場合400を返すこと(int quantity) throws Exception {
        InventoryProduct request = new InventoryProduct();
        request.setProductId(1);
        request.setQuantity(quantity);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String responseActual = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/received-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String responseExpected = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"quantity",
                         "message":"must be greater than or equal to 1"
                      }
                   ]
                }
                """;

        JSONAssert.assertEquals(responseExpected, responseActual, JSONCompareMode.STRICT);
    }

    @Test
    @Transactional
    void 存在しない商品IDで入庫処理時に404を返すこと() throws Exception {
        int productId = 0;
        InventoryProduct request = new InventoryProduct();
        request.setProductId(productId);
        request.setQuantity(500);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/received-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/inventory-products/received-items", JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("Product ID:" + productId + " does not exist", JsonPath.read(response, "$.message"));
    }

    @Test
    @ExpectedDataSet(value = "/dataset/expectedShippedInventoryProducts.yml", ignoreCols = "history")
    @Transactional
    void 出庫処理ができ201を返しレコードが登録されていること() throws Exception {
        InventoryProduct request = new InventoryProduct();
        request.setProductId(1);
        request.setQuantity(50);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/shipped-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString((StandardCharsets.UTF_8));

        JSONAssert.assertEquals("""
                        {
                           "message":"item was successfully shipped"
                        }
                        """
                , response, JSONCompareMode.STRICT);
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {0, -500})
    void 出庫処理時の数量が0以下の場合400を返すこと(int quantity) throws Exception {
        InventoryProduct request = new InventoryProduct();
        request.setProductId(1);
        request.setQuantity(quantity);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String responseActual = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/shipped-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String responseExpected = """
                {
                   "status":"BAD_REQUEST",
                   "message":"Bad request",
                   "errors":[
                      {
                         "field":"quantity",
                         "message":"must be greater than or equal to 1"
                      }
                   ]
                }
                """;

        JSONAssert.assertEquals(responseExpected, responseActual, JSONCompareMode.STRICT);
    }

    @Test
    @Transactional
    void 存在しない商品IDで出庫処理時に404を返すこと() throws Exception {
        int productId = 0;
        InventoryProduct request = new InventoryProduct();
        request.setProductId(productId);
        request.setQuantity(500);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/shipped-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/inventory-products/shipped-items", JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
        assertEquals("Product ID:" + productId + " does not exist", JsonPath.read(response, "$.message"));
    }

    @Test
    @Transactional
    void 在庫数より多い数で出庫処理時に409を返すこと() throws Exception {
        int productId = 1;
        InventoryProduct request = new InventoryProduct();
        request.setProductId(productId);
        int quantity = 500;
        request.setQuantity(quantity);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/inventory-products/shipped-items")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("/inventory-products/shipped-items", JsonPath.read(response, "$.path"));
        assertEquals("Conflict", JsonPath.read(response, "$.error"));
        assertEquals("Inventory shortage, only " + 100 + " items left", JsonPath.read(response, "$.message"));
    }
}
