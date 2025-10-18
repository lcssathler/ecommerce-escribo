package ecommerce_escribo.demo.domain;

import ecommerce_escribo.demo.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> listProductsToBuy;

    @NotBlank(message = "order must have a status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

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

    public void setListProductsToBuy(List<OrderProduct> listProductsToBuy) {
        this.listProductsToBuy = listProductsToBuy;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
