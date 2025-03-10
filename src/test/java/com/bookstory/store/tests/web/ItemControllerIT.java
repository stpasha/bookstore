package com.bookstory.store.tests.web;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@StoreTestAnnotation
@AutoConfigureMockMvc
class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;


    private static final Long ITEM_ID = 1L;


    @Test
    void shouldAddItemToCart() throws Exception {
        mockMvc.perform(post("/items/" + ITEM_ID + "/add")
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    void shouldRemoveItemFromCart() throws Exception {
        mockMvc.perform(post("/items/" + ITEM_ID + "/add")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/items/remove")
                        .param("productId", ITEM_ID.toString()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldViewCart() throws Exception {
        MvcResult result = mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Корзина товаров");
    }
}
