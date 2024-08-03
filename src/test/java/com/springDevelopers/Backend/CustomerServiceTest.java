package com.springDevelopers.Backend.Services;

import com.springDevelopers.Backend.DTO.AddProductToCartDTO;
import com.springDevelopers.Backend.DTO.CartDTO;
import com.springDevelopers.Backend.DTO.OrderDto;
import com.springDevelopers.Backend.DTO.PlaceOrderDto;
import com.springDevelopers.Backend.Entities.CartItems;
import com.springDevelopers.Backend.Entities.Order;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Enums.OrderStatus;
import com.springDevelopers.Backend.Repositories.CartItemsRepository;
import com.springDevelopers.Backend.Repositories.OrderRepository;
import com.springDevelopers.Backend.Repositories.ProductRepository;
import com.springDevelopers.Backend.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @InjectMocks
    private CustomerService customerService;

    public CustomerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToCart_Success() {
        AddProductToCartDTO addProductToCartDTO = new AddProductToCartDTO();
        addProductToCartDTO.setUserId(1);
        addProductToCartDTO.setProductId(1);
        addProductToCartDTO.setProductQuantity(2);

        Order activeOrder = new Order();
        activeOrder.setId(1);
        activeOrder.setTotalAmount(BigDecimal.ZERO);
        activeOrder.setOrderStatus(OrderStatus.PENDING);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(10.00));

        User user = new User();
        user.setId(1);

        CartItems cartItems = new CartItems();
        cartItems.setProduct(product);
        cartItems.setPrice(product.getPrice());
        cartItems.setQuantity(addProductToCartDTO.getProductQuantity());
        cartItems.setOrder(activeOrder);
        cartItems.setUser(user);

        when(orderRepository.findByUserIdAndOrderStatus(addProductToCartDTO.getUserId(), OrderStatus.PENDING))
                .thenReturn(activeOrder);
        when(productRepository.findById(addProductToCartDTO.getProductId())).thenReturn(Optional.of(product));
        when(userRepository.findById(addProductToCartDTO.getUserId())).thenReturn(Optional.of(user));
        when(cartItemsRepository.findByUserIdAndProductIdAndOrderId(
                addProductToCartDTO.getUserId(), addProductToCartDTO.getProductId(), activeOrder.getId()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = customerService.addProductToCart(addProductToCartDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartDTO cartDTO = (CartDTO) response.getBody();
        assertEquals(addProductToCartDTO.getProductQuantity(), cartDTO.getQuantity());
        assertEquals(product.getPrice(), cartDTO.getPrice());

        verify(cartItemsRepository).save(any(CartItems.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_Success() {
        PlaceOrderDto placeOrderDto = new PlaceOrderDto();
        placeOrderDto.setUserId(1);
        placeOrderDto.setOrderDescription("Test Order");
        placeOrderDto.setAddress("123 Test Street");

        Order activeOrder = new Order();
        activeOrder.setId(1);
        activeOrder.setOrderStatus(OrderStatus.PENDING);

        User user = new User();
        user.setId(1);

        when(orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.PENDING))
                .thenReturn(activeOrder);
        when(userRepository.findById(placeOrderDto.getUserId())).thenReturn(Optional.of(user));

        OrderDto orderDto = customerService.placeOrder(placeOrderDto);

        assertEquals(placeOrderDto.getOrderDescription(), orderDto.getOrderDescription());
        assertEquals(placeOrderDto.getAddress(), orderDto.getAddress());
        assertEquals(OrderStatus.PLACED, orderDto.getOrderStatus());
        assertEquals(1, orderDto.getUserId());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testAddProductToCart_NoActiveOrder() {
        AddProductToCartDTO addProductToCartDTO = new AddProductToCartDTO();
        addProductToCartDTO.setUserId(1);
        addProductToCartDTO.setProductId(1);

        when(orderRepository.findByUserIdAndOrderStatus(addProductToCartDTO.getUserId(), OrderStatus.PENDING))
                .thenReturn(null);

        ResponseEntity<?> response = customerService.addProductToCart(addProductToCartDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No active order found for the user", response.getBody());
    }
}
