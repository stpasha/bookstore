package com.bookstory.store.tests.web;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.util.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.servlet.MockMvc;

@StoreTestAnnotation
@WebFluxTest
class DefaultControllerIT {

    @Autowired
    private ProductService productService;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataFactory testDataFactory;


    private static final Long ITEM_ID = 1L;

//    @Nested
//    class ProductControllerIT {
//        @Test
//        void listProducts() throws Exception {
//            mockMvc.perform(get("/products"))
//                    .andExpect(status().isOk())
//                    .andExpect(view().name("main"))
//                    .andExpect(model().attributeExists("products"))
//                    .andExpect(model().attributeExists("productPage"));
//        }
//
//        @Test
//        void getProduct() throws Exception {
//            Long productId = 1L;
//            ProductDTO productDTO = new ProductDTO();
//            productDTO.setId(productId);
//            productDTO.setTitle("Java: The Complete Reference");
//
//            Optional<ProductDTO> product = productService.getProduct(productId);
//
//            mockMvc.perform(get("/products/{id}", productId))
//                    .andExpect(status().isOk())
//                    .andExpect(view().name("item"))
//                    .andExpect(model().attributeExists("product"))
//                    .andExpect(model().attribute("product", product.get()));
//        }
//    }
//
//    @Nested
//    class ItemControllerIT {
//        @Test
//        void shouldAddItemToCart() throws Exception {
//            mockMvc.perform(post("/items/" + ITEM_ID + "/add")
//                            .param("quantity", "2")
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl("/products"));
//        }
//
//        @Test
//        void shouldRemoveItemFromCart() throws Exception {
//            mockMvc.perform(post("/items/" + ITEM_ID + "/add")
//                            .param("quantity", "2"))
//                    .andExpect(status().is3xxRedirection());
//
//            mockMvc.perform(post("/items/remove")
//                            .param("productId", ITEM_ID.toString()))
//                    .andExpect(status().is3xxRedirection());
//        }
//
//        @Test
//        void shouldViewCart() throws Exception {
//            MvcResult result = mockMvc.perform(get("/items"))
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//            String content = result.getResponse().getContentAsString();
//            assertThat(content).contains("Корзина товаров");
//        }
//    }
//
//    @Nested
//    class OrderControllerIT {
//        @Test
//        void shouldReturnOrdersList() throws Exception {
//            mockMvc.perform(get("/orders"))
//                    .andExpect(status().isOk())
//                    .andExpect(view().name("orders"))
//                    .andExpect(model().attributeExists("orders"));
//        }
//
//        @Test
//        void shouldReturnOrderDetails() throws Exception {
//            OrderDTO order = orderService.createOrder(testDataFactory.createOrderDTO()).orElseThrow();
//            mockMvc.perform(get("/orders/{id}", order.getId()))
//                    .andExpect(status().isOk())
//                    .andExpect(view().name("order"))
//                    .andExpect(model().attributeExists("order"));
//        }
//
//        @Test
//        void shouldReturnErrorWhenOrderNotFound() throws Exception {
//            mockMvc.perform(get("/orders/{id}", 9999L))
//                    .andExpect(status().isOk())
//                    .andExpect(view().name("error"))
//                    .andExpect(model().attributeExists("error"));
//        }
//    }
}
