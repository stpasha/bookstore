package com.bookstory.store.web;

import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

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

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile imageFile, Model model) {
        if (imageFile.isEmpty()) {
            model.addAttribute("error", "file is empty");
            return "error";
        }

        if (!imageFile.getContentType().equals("text/csv")) {
            model.addAttribute("error", "Not CSV-file");
            return "error";
        }

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(imageFile.getInputStream(), StandardCharsets.UTF_8))) {
            var lines = csvReader.readAll();
            if (lines.isEmpty()) {
                model.addAttribute("error", "CSV-file is empty");
                return "error";
            }

            productService.addProducts(lines.stream().skip(1)
                    .filter(line -> line.length >= 5)
                    .map(line -> NewProductDTO.builder()
                            .title(line[0])
                            .description(line[1])
                            .price(new BigDecimal(line[2]))
                            .imageName(line[3])
                            .baseImage(line[4])
                            .build()));

        } catch (IOException | CsvException e) {
            model.addAttribute("error", "Error during upload of a file: " + e.getMessage());
            return "error";
        }

        return "main";
    }

}
