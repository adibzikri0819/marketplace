package com.anycomp.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "sellers")
public class Seller {
    
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
    
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("seller-items")
    private List<Item> items = new ArrayList<>();
    
    // Default constructor
    public Seller() {}
    
    // Constructor with parameters
    public Seller(String name, String email) {
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
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "Seller{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
