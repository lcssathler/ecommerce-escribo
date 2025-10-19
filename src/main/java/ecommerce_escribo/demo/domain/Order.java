package ecommerce_escribo.demo.domain;

import ecommerce_escribo.demo.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "oder must reference a client")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotNull
    @DecimalMin(value = "0.0", message = "total can't be negative")
    @Column
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> listProductsToBuy = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Order() {
    }

    public Order(Client client) {
        this.client = client;
    }

    public void addItem(OrderProduct orderProduct) {
        orderProduct.setOrder(this);
        this.listProductsToBuy.add(orderProduct);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.total = listProductsToBuy.stream()
                .map(OrderProduct::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderProduct> getListProductsToBuy() {
        return listProductsToBuy;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
