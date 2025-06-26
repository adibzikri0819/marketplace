package com.anycomp.marketplace.controller;

import com.anycomp.marketplace.entity.Seller;
import com.anycomp.marketplace.service.SellerService;
import com.anycomp.marketplace.service.ItemService;
import com.anycomp.marketplace.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = "*")
public class SellerController {
    
    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private PurchaseService purchaseService;
    
    // GET /sellers - List all sellers
    @GetMapping
    public ResponseEntity<Page<Seller>> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Seller> sellers = sellerService.getAllSellers(pageable);
        
        return ResponseEntity.ok(sellers);
    }
    
    // GET /sellers/{id} - Get a specific seller
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        Optional<Seller> seller = sellerService.getSellerById(id);
        
        if (seller.isPresent()) {
            return ResponseEntity.ok(seller.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /sellers - Create a seller
    @PostMapping
    public ResponseEntity<Seller> createSeller(@Valid @RequestBody Seller seller) {
        try {
            Seller createdSeller = sellerService.createSeller(seller);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSeller);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /sellers/{id} - Update a seller
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, 
                                             @Valid @RequestBody Seller sellerDetails) {
        try {
            Seller updatedSeller = sellerService.updateSeller(id, sellerDetails);
            return ResponseEntity.ok(updatedSeller);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /sellers/{id} - Delete a seller
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        try {
            sellerService.deleteSeller(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // GET /sellers/search - Search sellers by name
    @GetMapping("/search")
    public ResponseEntity<Page<Seller>> searchSellers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Seller> sellers = sellerService.searchSellersByName(name, pageable);
        
        return ResponseEntity.ok(sellers);
    }
    
    // GET /sellers/{sellerId}/items - Get items by seller
    @GetMapping("/{sellerId}/items")
    public ResponseEntity<?> getItemsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        
        if (!sellerService.sellerExists(sellerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (availableOnly) {
            return ResponseEntity.ok(itemService.getAvailableItemsBySellerId(sellerId, pageable));
        } else {
            return ResponseEntity.ok(itemService.getItemsBySellerId(sellerId, pageable));
        }
    }
    
    // POST /sellers/{sellerId}/items - Add new item to seller
    @PostMapping("/{sellerId}/items")
    public ResponseEntity<?> addItemToSeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody ItemRequest itemRequest) {
        
        if (!sellerService.sellerExists(sellerId)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // Convert request to Item entity
            com.anycomp.marketplace.entity.Item item = new com.anycomp.marketplace.entity.Item();
            item.setName(itemRequest.getName());
            item.setDescription(itemRequest.getDescription());
            item.setPrice(itemRequest.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            
            com.anycomp.marketplace.entity.Item createdItem = itemService.createItem(item, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // GET /sellers/{id}/revenue - Get total revenue for a seller
    @GetMapping("/{id}/revenue")
    public ResponseEntity<?> getSellerTotalRevenue(@PathVariable Long id) {
        if (!sellerService.sellerExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Double totalRevenue = purchaseService.getTotalRevenueForSeller(id);
        return ResponseEntity.ok(new RevenueResponse(totalRevenue));
    }
    
    // GET /sellers/{id}/sales - Get sales for a specific seller
    @GetMapping("/{id}/sales")
    public ResponseEntity<?> getSellerSales(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (!sellerService.sellerExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
        return ResponseEntity.ok(purchaseService.getPurchasesBySellerId(id, pageable));
    }
    
    // GET /sellers/with-items - Get sellers who have items
    @GetMapping("/with-items")
    public ResponseEntity<Page<Seller>> getSellersWithItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Seller> sellers = sellerService.getSellersWithItems(pageable);
        
        return ResponseEntity.ok(sellers);
    }
    
    // GET /sellers/with-sales - Get sellers who have made sales
    @GetMapping("/with-sales")
    public ResponseEntity<Page<Seller>> getSellersWithSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Seller> sellers = sellerService.getSellersWithSales(pageable);
        
        return ResponseEntity.ok(sellers);
    }
    
    // Inner classes for request/response
    public static class ItemRequest {
        private String name;
        private String description;
        private Double price;
        private Integer quantity;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    public static class RevenueResponse {
        private Double totalRevenue;
        
        public RevenueResponse(Double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
        
        public Double getTotalRevenue() {
            return totalRevenue;
        }
        
        public void setTotalRevenue(Double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }
}
