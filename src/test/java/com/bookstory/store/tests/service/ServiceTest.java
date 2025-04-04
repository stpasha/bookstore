package com.bookstory.store.tests.service;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.repository.ItemRepository;
import com.bookstory.store.repository.ListItemRepository;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.repository.ProductRepository;
import com.bookstory.store.service.DefaultItemService;
import com.bookstory.store.service.FileService;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.tests.AbstractTest;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import com.bookstory.store.web.mapper.NewProductMapper;
import com.bookstory.store.web.mapper.OrderMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@StoreTestAnnotation
@Slf4j
public class ServiceTest extends AbstractTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private NewProductMapper newProductMapper;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private FileService fileService;

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

    @MockitoBean
    private ListItemRepository listItemRepository;

    @Autowired
    private ItemMapper itemMapper;


    @Nested
    class ProductServiceTest {


        @Test
        public void getAllProducts_ShouldReturnPagedProducts_WhenNoFilter() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = testDataFactory.createProducts(3);

            Flux<Product> productFlux = Flux.fromIterable(products);
            Mono<Long> countMono = Mono.just((long) products.size());

            when(productRepository.count()).thenReturn(countMono);
            when(productRepository.findAllBy(pageable)).thenReturn(productFlux);

            Mono<Page<ProductDTO>> resultMono = productService.getAllProducts("", pageable);

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertEquals(3, result.getTotalElements(), "Total elements should match");
                        verify(productRepository, times(1)).findAllBy(pageable);
                        verify(productRepository, times(1)).count();
                    })
                    .verifyComplete();
        }

        @Test
        public void getAllProducts_ShouldReturnFilteredProducts_WhenTitleProvided() {
            Pageable pageable = PageRequest.of(0, 10);
            String title = "Test Book";
            List<Product> products = testDataFactory.createProducts(2).stream().peek(product -> product.setTitle(title)).toList();

            when(productRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(Flux.fromIterable(products));
            when(productRepository.countByTitleContainingIgnoreCase(title)).thenReturn(Mono.just((long) products.size()));


            Mono<Page<ProductDTO>> resultMono = productService.getAllProducts(title, pageable);
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertEquals(2, result.getTotalElements());
                        verify(productRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
                    })
                    .verifyComplete();

        }

        @Test
        public void getProduct_ShouldReturnProduct_WhenExists() {
            Product product = TestDataFactory.PRODUCTS.get(0);

            when(productRepository.findById(anyLong())).thenReturn(Mono.just(product));

            Mono<ProductDTO> result = productService.getProduct(1L);

            StepVerifier.create(result)
                    .assertNext(productDTO -> assertEquals(product.getTitle(), productDTO.getTitle()))
                    .verifyComplete();

            verify(productRepository, times(1)).findById(1L);
        }

    }

    @Nested
    class OrderServiceTest {

        @Autowired
        private CacheManager cacheManager;

        @BeforeEach
        void clearCache() {
            Cache cache = cacheManager.getCache("products");
            if (cache != null) {
                cache.clear();
            }
        }

        @Test
        public void createOrder() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            orderDTO.setCreatedAt(LocalDateTime.now());
            orderDTO.setUpdatedAt(LocalDateTime.now());

            Order order = orderMapper.toEntity(orderDTO);

            when(productRepository.findById(anyLong()))
                    .thenAnswer(invocation -> {
                        Long productId = invocation.getArgument(0);
                        return Flux.fromIterable(order.getItems())
                                .map(Item::getProduct)
                                .filter(product -> product.getId().equals(productId))
                                .next();
                    });
            when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
            when(itemRepository.saveAll(any(Publisher.class))).thenReturn(Flux.fromIterable(order.getItems()));
            StepVerifier.create(orderService.createOrder(Mono.just(orderDTO)))
                    .assertNext(resOrderDTO -> {
                        assertNotNull(resOrderDTO, "Order should be created");
                        assertEquals(order.getId(), resOrderDTO.getId());
                        assertEquals(order.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                                resOrderDTO.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
                        assertEquals(order.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS),
                                resOrderDTO.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS));
                        assertEquals(order.getComment(), resOrderDTO.getComment());
                    })
                    .verifyComplete();
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(productRepository, times(order.getItems().size())).findById(anyLong());
        }

        @Test
        public void createOrderThrowsWhenProductNotFound() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();

            when(productRepository.findById(anyLong())).thenReturn(Mono.empty());
            StepVerifier.create(orderService.createOrder(Mono.just(orderDTO))).expectError(IllegalArgumentException.class).verify();


            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        public void getOrder() {
            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            Order order = orderMapper.toEntity(orderDTO);

            when(orderRepository.findById(order.getId())).thenReturn(Mono.just(order));
            when(listItemRepository.findByOrdersId(order.getId())).thenReturn(Flux.fromIterable(order.getItems()));

            StepVerifier.create(orderService.getOrder(orderDTO.getId())).assertNext(result -> {
                assertNotNull(result);
                assertEquals(orderDTO.getId(), result.getId());
                assertEquals(orderDTO.getCreatedAt(), result.getCreatedAt());
                assertEquals(orderDTO.getUpdatedAt(), result.getUpdatedAt());
                assertEquals(orderDTO.getComment(), result.getComment());
            }).verifyComplete();



            verify(orderRepository, times(1)).findById(order.getId());
        }

        @Test
        public void getOrders() {
            List<OrderDTO> orderDTOs = testDataFactory.createOrderDTOs(5);
            List<Order> orders = orderDTOs.stream().map(orderMapper::toEntity).toList();

            when(orderRepository.findAll()).thenReturn(Flux.fromIterable(orders));

            StepVerifier.create(orderService.getAllOrders()).expectNextCount(orderDTOs.size()).verifyComplete();

            verify(orderRepository, times(1)).findAll();
        }
    }

    @Nested
    class ItemServiceTest {


        @Test
        public void createItems() {
            List<ItemDTO> itemDTOs = testDataFactory.createItemDTOs(3);

            itemDTOs.forEach(itemDTO -> {
                when(productRepository.findById(itemDTO.getProduct().getId()))
                        .thenReturn(Mono.just(productMapper.toEntity(itemDTO.getProduct())));
            });

            when(itemRepository.saveAll(any(Publisher.class))).thenAnswer(invocation -> {
                Publisher<Item> publisher = invocation.getArgument(0);
                return Flux.from(publisher);
            });

            StepVerifier.create(itemService.createItems(Flux.fromIterable(itemDTOs)))
                    .expectNextCount(itemDTOs.size())
                    .verifyComplete();

            verify(itemRepository, times(1)).saveAll(any(Publisher.class));
        }

    }

}
