package com.bookstory.store.tests.service;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.service.DefaultItemService;
import com.bookstory.store.service.ImageService;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import com.bookstory.store.web.mapper.NewProductMapper;
import com.bookstory.store.web.mapper.OrderMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@StoreTestAnnotation
public class ServiceTest {

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

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    private DefaultItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;


    @Nested
    class ProductServiceTest {


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

    @Nested
    class OrderServiceTest {

        @Test
        public void createOrder() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            Order order = orderMapper.toEntity(orderDTO);

            order.getItems().forEach(item ->
                    when(productRepository.findById(item.getProduct().getId()))
                            .thenReturn(Optional.of(item.getProduct()))
            );

            when(orderRepository.save(any(Order.class))).thenReturn(order);

            Optional<OrderDTO> outOrderDTO = orderService.createOrder(orderDTO);

            assertTrue(outOrderDTO.isPresent(), "Order should be created");
            OrderDTO result = outOrderDTO.get();

            assertEquals(order.getId(), result.getId());
            assertEquals(order.getCreatedAt(), result.getCreatedAt());
            assertEquals(order.getUpdatedAt(), result.getUpdatedAt());
            assertEquals(order.getComment(), result.getComment());

            verify(orderRepository, times(1)).save(any(Order.class));
            verify(productRepository, times(order.getItems().size())).findById(anyLong());
        }

        @Test
        public void createOrderThrowsWhenProductNotFound() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();

            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDTO));

            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        public void getOrder() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            Order order = orderMapper.toEntity(orderDTO);

            when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

            Optional<OrderDTO> outOrderDTO = orderService.getOrder(orderDTO.getId());

            assertTrue(outOrderDTO.isPresent());
            OrderDTO result = outOrderDTO.get();

            assertEquals(orderDTO.getId(), result.getId());
            assertEquals(orderDTO.getCreatedAt(), result.getCreatedAt());
            assertEquals(orderDTO.getUpdatedAt(), result.getUpdatedAt());
            assertEquals(orderDTO.getComment(), result.getComment());

            verify(orderRepository, times(1)).findById(order.getId());
        }

        @Test
        public void getOrders() {
            List<OrderDTO> orderDTOs = testDataFactory.createOrderDTOs(5);
            List<Order> orders = orderDTOs.stream().map(orderMapper::toEntity).toList();

            when(orderRepository.findAll()).thenReturn(orders);

            List<OrderDTO> outOrderDTOs = orderService.getAllOrders();

            assertEquals(orderDTOs.size(), outOrderDTOs.size());

            verify(orderRepository, times(1)).findAll();
        }
    }

    @Nested
    class ItemServiceTest {


        @Test
        public void createItems() {
            List<ItemDTO> itemDTOs = testDataFactory.createItemDTOs(3);
            List<Item> items = itemDTOs.stream().map(itemMapper::toEntity).toList();

            itemDTOs.forEach(itemDTO -> {
                when(productRepository.findById(itemDTO.getProduct().getId()))
                        .thenReturn(Optional.of(productMapper.toEntity(itemDTO.getProduct())));
            });

            when(itemRepository.saveAll(items)).thenReturn(items);

            List<ItemDTO> result = itemService.createItems(itemDTOs);

            assertEquals(itemDTOs.size(), result.size());

            verify(itemRepository, times(1)).saveAll(anyList());
        }

    }
}
