package ecommerce_escribo.demo.service;

import ecommerce_escribo.demo.domain.Client;
import ecommerce_escribo.demo.domain.Order;
import ecommerce_escribo.demo.domain.OrderProduct;
import ecommerce_escribo.demo.domain.Product;
import ecommerce_escribo.demo.dto.OrderDTO;
import ecommerce_escribo.demo.dto.OrderProductDTO;
import ecommerce_escribo.demo.enums.Status;
import ecommerce_escribo.demo.mapper.OrderMapper;
import ecommerce_escribo.demo.repository.ClientRepository;
import ecommerce_escribo.demo.repository.OrderProductRepository;
import ecommerce_escribo.demo.repository.OrderRepository;
import ecommerce_escribo.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private OrderMapper mapper;



    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        Client client = clientRepository.findById(orderDTO.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Order order = new Order(client);

        if (orderDTO.listProductsToBuy() == null) {
            throw new IllegalArgumentException("List of products can't be empty");
        }
        for (OrderProductDTO orderProductDTO : orderDTO.listProductsToBuy()) {
            Product product = productRepository.findById(orderProductDTO.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + orderProductDTO.productId()));

            if (product.getQuantity() < orderProductDTO.quantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - orderProductDTO.quantity());
            productRepository.save(product);

            order.addItem(new OrderProduct(product, orderProductDTO.quantity()));
        }

        order.recalculateTotal();
        Order saved = orderRepository.save(order);
        return mapper.toDTO(saved);
    }

    public OrderDTO update(Long orderId, OrderDTO orderDTO) {
        Order order = validateClient(orderId, orderDTO);

        Map<Long, OrderProduct> oldListProducts = order.getListProductsToBuy().stream()
                .collect(Collectors.toMap(op -> op.getProduct().getId(), op -> op));

        BigDecimal newTotal = BigDecimal.ZERO;
        List<OrderProduct> updatedItems = new ArrayList<>();

        for (OrderProductDTO itemDTO : orderDTO.listProductsToBuy()) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDTO.productId()));

            int requestedQty = itemDTO.quantity();

            if (oldListProducts.containsKey(product.getId())) {
                OrderProduct existing = oldListProducts.get(product.getId());
                int oldQty = existing.getQuantity();

                if (requestedQty > oldQty) {
                    int diff = requestedQty - oldQty;
                    if (product.getQuantity() < diff) {
                        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                    }
                    product.setQuantity(product.getQuantity() - diff);
                }
                else if (requestedQty < oldQty) {
                    int diff = oldQty - requestedQty;
                    product.setQuantity(product.getQuantity() + diff);
                }

                existing.setQuantity(requestedQty);
                existing.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(requestedQty)));
                updatedItems.add(existing);
                oldListProducts.remove(product.getId());
            }
            else {
                if (product.getQuantity() < requestedQty) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                product.setQuantity(product.getQuantity() - requestedQty);

                OrderProduct newOrderProduct = new OrderProduct(
                        order, product, requestedQty, product.getPrice(),
                        product.getPrice().multiply(BigDecimal.valueOf(requestedQty))
                );

                updatedItems.add(newOrderProduct);
            }

            newTotal = newTotal.add(product.getPrice().multiply(BigDecimal.valueOf(requestedQty)));
        }

        for (OrderProduct removed : oldListProducts.values()) {
            Product product = removed.getProduct();
            product.setQuantity(product.getQuantity() + removed.getQuantity());
            orderProductRepository.delete(removed);
        }

        order.getListProductsToBuy().clear();
        order.getListProductsToBuy().addAll(updatedItems);

        order.setTotal(newTotal);

        if (orderDTO.status() != null) {
            order.setStatus(Status.valueOf(orderDTO.status()));
        }

        orderRepository.save(order);
        return mapper.toDTO(order);
    }

    private Order validateClient(Long orderId, OrderDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (!order.getClient().getId().equals(dto.clientId())) {
            Client client = clientRepository.findById(dto.clientId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + dto.clientId()));
            order.setClient(client);
        }
        return order;
    }

    @Transactional
    public OrderDTO updateStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

        if (order.getStatus() == Status.APPROVED || order.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Cannot update a completed or canceled order");
        }

        Status targetStatus;
        try {
            targetStatus = Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        if (targetStatus == Status.CANCELLED) {
            for (OrderProduct orderProduct : order.getListProductsToBuy()) {
                Product product = orderProduct.getProduct();
                product.setQuantity(product.getQuantity() + orderProduct.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(targetStatus);
        Order updated = orderRepository.save(order);
        return mapper.toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

        if (order.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Only pending orders can be deleted");
        }

        for (OrderProduct orderProduct : order.getListProductsToBuy()) {
            Product product = orderProduct.getProduct();
            product.setQuantity(product.getQuantity() + orderProduct.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }

    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        return mapper.toDTO(order);
    }
}
