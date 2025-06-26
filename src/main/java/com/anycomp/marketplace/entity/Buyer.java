package com.anycomp.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "buyers")
public class Buyer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;
    
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("buyer-purchases")
    private List<Purchase> purchasedItems = new ArrayList<>();
    
    // Default constructor
    public Buyer() {}
    
    // Constructor with parameters
    public Buyer(String name, String email) {
        this.name = name;
        this.email = email;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Purchase> getPurchasedItems() {
        return purchasedItems;
    }
    
    public void setPurchasedItems(List<Purchase> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }
    
    @Override
    public String toString() {
        return "Buyer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
