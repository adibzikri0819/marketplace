package com.anycomp.marketplace.controller;

import com.anycomp.marketplace.entity.Purchase;
import com.anycomp.marketplace.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    // DTO for purchase request
    public static class PurchaseRequest {
        @NotNull(message = "Buyer ID is required")
        private Long buyerId;
        
        @NotNull(message = "Item ID is required")
        private Long itemId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        // Constructors
        public PurchaseRequest() {}
        
        public PurchaseRequest(Long buyerId, Long itemId, Integer quantity) {
            this.buyerId = buyerId;
            this.itemId = itemId;
            this.quantity = quantity;
        }
        
        // Getters and Setters
        public Long getBuyerId() { return buyerId; }
        public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
        
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    // Create a new purchase
    @PostMapping
    public ResponseEntity<?> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        try {
            Purchase purchase = purchaseService.buyItem(
                request.getBuyerId(), 
                request.getItemId(), 
                request.getQuantity()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Purchase created successfully");
            response.put("purchase", purchase);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // Get all purchases with pagination
    @GetMapping
    public ResponseEntity<Page<Purchase>> getAllPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getAllPurchases(pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Get purchase by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseById(@PathVariable Long id) {
        Optional<Purchase> purchase = purchaseService.getPurchaseById(id);
        
        if (purchase.isPresent()) {
            return ResponseEntity.ok(purchase.get());
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Purchase not found with id: " + id);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    // Get purchases by buyer ID
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<Page<Purchase>> getPurchasesByBuyer(
            @PathVariable Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getPurchasesByBuyerId(buyerId, pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Get purchases by item ID
    @GetMapping("/item/{itemId}")
    public ResponseEntity<Page<Purchase>> getPurchasesByItem(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getPurchasesByItemId(itemId, pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Get purchases by seller ID
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Purchase>> getPurchasesBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getPurchasesBySellerId(sellerId, pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Get purchases by date range
    @GetMapping("/date-range")
    public ResponseEntity<Page<Purchase>> getPurchasesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getPurchasesByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Get recent purchases
    @GetMapping("/recent")
    public ResponseEntity<Page<Purchase>> getRecentPurchases(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Purchase> purchases = purchaseService.getRecentPurchases(days, pageable);
        
        return ResponseEntity.ok(purchases);
    }
    
    // Cancel purchase
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelPurchase(@PathVariable Long id) {
        try {
            purchaseService.cancelPurchase(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Purchase cancelled successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // Validate purchase request
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePurchaseRequest(
            @Valid @RequestBody PurchaseRequest request) {
        
        boolean isValid = purchaseService.validatePurchaseRequest(
            request.getBuyerId(), 
            request.getItemId(), 
            request.getQuantity()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("message", isValid ? "Purchase request is valid" : "Purchase request is invalid");
        
        return ResponseEntity.ok(response);
    }
    
    // Get statistics endpoints
    
    // Get total revenue for seller
    @GetMapping("/stats/seller/{sellerId}/revenue")
    public ResponseEntity<Map<String, Object>> getSellerRevenue(@PathVariable Long sellerId) {
        Double revenue = purchaseService.getTotalRevenueForSeller(sellerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sellerId", sellerId);
        response.put("totalRevenue", revenue);
        
        return ResponseEntity.ok(response);
    }
    
    // Get total spending for buyer
    @GetMapping("/stats/buyer/{buyerId}/spending")
    public ResponseEntity<Map<String, Object>> getBuyerSpending(@PathVariable Long buyerId) {
        Double spending = purchaseService.getTotalSpendingForBuyer(buyerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("buyerId", buyerId);
        response.put("totalSpending", spending);
        
        return ResponseEntity.ok(response);
    }
    
    // Get most purchased items
    @GetMapping("/stats/most-purchased")
    public ResponseEntity<Map<String, Object>> getMostPurchasedItems() {
        List<Object[]> items = purchaseService.getMostPurchasedItems();
        
        Map<String, Object> response = new HashMap<>();
        response.put("mostPurchasedItems", items);
        
        return ResponseEntity.ok(response);
    }
    
    // Get total purchases count
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Object>> getTotalPurchasesCount() {
        long count = purchaseService.getTotalPurchasesCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalPurchases", count);
        
        return ResponseEntity.ok(response);
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "PurchaseController");
        
        return ResponseEntity.ok(response);
    }
}