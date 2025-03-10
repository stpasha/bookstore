package com.bookstory.store.tests.web;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@StoreTestAnnotation
@AutoConfigureMockMvc
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Test
    void listProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("productPage"));
    }

    @Test
    void getProduct() throws Exception {
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setTitle("Java: The Complete Reference");

        Optional<ProductDTO> product = productService.getProduct(productId);

        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", product.get()));
    }
}
