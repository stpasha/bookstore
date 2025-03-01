package com.bookstory.store.web;

import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.mapper.ProductMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
    }

    @ModelAttribute
    @GetMapping
    public String listProducts(@PageableDefault(size = 10, sort = "id") Pageable pageable, @RequestParam(value = "title", required = false) String title, Model model) {
        model.addAttribute("products", productService.getAllProducts(title, pageable));
        return "main";
    }

    @ModelAttribute
    @GetMapping("/{id}")
    public String getProduct(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));

        return "item";
    }
}
