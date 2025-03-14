package com.bookstory.store.tests.service;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.service.ImageService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.NewProductMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@StoreTestAnnotation
public class ProductServiceTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private NewProductMapper newProductMapper;

    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private ImageService imageService;
    @Autowired
    private ProductService productService;

    @Test
    public void getAllProducts_ShouldReturnPagedProducts_WhenNoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = testDataFactory.createProducts(3);
        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.getAllProducts(null, pageable);

        assertEquals(3, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getAllProducts_ShouldReturnFilteredProducts_WhenTitleProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        String title = "Test Book";
        List<Product> products = testDataFactory.createProducts(2);
        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.getAllProducts(title, pageable);

        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }

    @Test
    public void getProduct_ShouldReturnProduct_WhenExists() {
        Product product = testDataFactory.createProduct();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Optional<ProductDTO> result = productService.getProduct(1L);

        assertTrue(result.isPresent());
        assertEquals(product.getTitle(), result.get().getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void getProduct_ShouldReturnEmpty_WhenNotExists() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.getProduct(999L);

        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    public void addProducts_ShouldThrowException_WhenImageProcessingFails() throws IOException {
        List<NewProductDTO> newProductDTOs = testDataFactory.createNewProductDTOs(1).stream()
                .peek(dto -> dto.setBaseImage(Base64.getEncoder().encodeToString(new byte[10])))
                .toList();

        when(imageService.saveImage(anyString(), any(byte[].class))).thenThrow(new IOException("Failed to process image"));

        assertThrows(RuntimeException.class, () -> productService.addProducts(newProductDTOs));

        verify(productRepository, never()).saveAll(anyList());
    }
}