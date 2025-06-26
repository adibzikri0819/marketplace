package com.anycomp.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonBackReference("buyer-purchases")
    private Buyer buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonBackReference("item-purchases")
    private Item item;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private LocalDateTime purchaseDate;
    
    @Column(nullable = false)
    private Double totalPrice;
    
    // Default constructor
    public Purchase() {
        this.purchaseDate = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public Purchase(Buyer buyer, Item item, Integer quantity) {
        this.buyer = buyer;
        this.item = item;
        this.quantity = quantity;
        this.purchaseDate = LocalDateTime.now();
        this.totalPrice = item.getPrice() * quantity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Buyer getBuyer() {
        return buyer;
    }
    
    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        // Update total price when quantity changes
        if (this.item != null) {
            this.totalPrice = this.item.getPrice() * quantity;
        }
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", purchaseDate=" + purchaseDate +
                ", totalPrice=" + totalPrice +
                '}';
    }
}