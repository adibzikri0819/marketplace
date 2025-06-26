package com.anycomp.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "items")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    @Column(nullable = false)
    private Integer quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonBackReference("seller-items")
    private Seller seller;
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("item-purchases")
    private List<Purchase> purchases = new ArrayList<>();
    
    // Default constructor
    public Item() {}
    
    // Constructor with parameters
    public Item(String name, String description, Double price, Integer quantity, Seller seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Seller getSeller() {
        return seller;
    }
    
    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    public List<Purchase> getPurchases() {
        return purchases;
    }
    
    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
