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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemsRepository cartItemsRepository;

    public ResponseEntity<?> addProductToCart(AddProductToCartDTO addProductToCartDTO) {
        Order activeOrder = this.orderRepository.findByUserIdAndOrderStatus(addProductToCartDTO.getUserId(),
                OrderStatus.PENDING);
        if (activeOrder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active order found for the user");
        }

        Optional<CartItems> cartItem = this.cartItemsRepository.findByUserIdAndProductIdAndOrderId(
                addProductToCartDTO.getUserId(), addProductToCartDTO.getProductId(), activeOrder.getId()
        );

        if (cartItem.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cart item already added");
        } else {
            Optional<Product> product = this.productRepository.findById(addProductToCartDTO.getProductId());
            Optional<User> user = this.userRepository.findById(addProductToCartDTO.getUserId());

            if (product.isPresent() && user.isPresent()) {
                CartItems cartItems = new CartItems();
                cartItems.setProduct(product.get());
                cartItems.setPrice(product.get().getPrice());
                cartItems.setQuantity(addProductToCartDTO.getProductQuantity());
                cartItems.setOrder(activeOrder);
                cartItems.setUser(user.get());
                this.cartItemsRepository.save(cartItems);

                CartDTO cartDTO = convertToCart(cartItems);
                BigDecimal totalCost = BigDecimal.valueOf(addProductToCartDTO.getProductId()).multiply(cartItems.getPrice());
                activeOrder.setTotalAmount(activeOrder.getTotalAmount().add(totalCost));
                activeOrder.getCartItemsList().add(cartItems);
                this.orderRepository.save(activeOrder);

                return ResponseEntity.status(HttpStatus.OK).body(cartDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product or user does not exist");
            }
        }
    }

    public OrderDto placeOrder(PlaceOrderDto placeOrderDto){
        Order activeOrder = this.orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(),
                OrderStatus.PENDING);
        Optional<User> user = this.userRepository.findById(placeOrderDto.getUserId());

       if(activeOrder == null){
           throw new NullPointerException("Order with status pending not found");
       }
        if(user.isPresent()){
            activeOrder.setOrderStatus(OrderStatus.PLACED);
            activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
            activeOrder.setOrderDate(new Date());
            activeOrder.setAddress(placeOrderDto.getAddress());
            this.orderRepository.save(activeOrder);
            Order  order = new Order();
            order.setTotalAmount(BigDecimal.valueOf(0.00));
            order.setOrderStatus(OrderStatus.PENDING);
            order.setUser(user.get());
            orderRepository.save(order);
            OrderDto orderDto = placeOrderDto(activeOrder);
            return orderDto;

        }
        return null;
    }

    private CartDTO convertToCart(CartItems cartItems) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cartItems.getId());
        cartDTO.setProductName(cartItems.getProduct().getName());
        cartDTO.setUserId(cartItems.getUser().getId());
        cartDTO.setPrice(cartItems.getPrice());
        cartDTO.setQuantity(cartItems.getQuantity());
        return cartDTO;
    }
    private  OrderDto placeOrderDto(Order order){
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderDto.getId());
        orderDto.setAddress(order.getAddress());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setOrderStatus(order.getOrderStatus());
        order.setOrderDescription(order.getOrderDescription());
        orderDto.setOrderDate(order.getOrderDate());
        return orderDto;

    }
}
