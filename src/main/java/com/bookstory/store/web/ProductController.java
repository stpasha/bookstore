package com.bookstory.store.web;

import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Size;

@Controller
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(@PageableDefault(size = 10, sort = "id") Pageable pageable,
                               @RequestParam(value = "search", required = false)
                               @Size(max = 255, message = "Title must be less than 255 characters") String title,
                               Model model) {
        log.info("list of products with params title {}, pageable {}", title, pageable);
        Page<ProductDTO> productPage = productService.getAllProducts(title, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("search", title);

        return "main";
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        return productService.getProduct(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    log.info("products queried {}", product);
                    return "item";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Product not found");
                    log.error("no value for productId {}", id);
                    return "error";
                });
    }
}
