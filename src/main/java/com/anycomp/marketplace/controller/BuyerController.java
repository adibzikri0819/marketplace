package com.anycomp.marketplace.controller;

import com.anycomp.marketplace.entity.Buyer;
import com.anycomp.marketplace.service.BuyerService;
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
@RequestMapping("/api/buyers")
@CrossOrigin(origins = "*")
public class BuyerController {
    
    @Autowired
    private BuyerService buyerService;
    
    @Autowired
    private PurchaseService purchaseService;
    
    // GET /buyers - List all buyers
    @GetMapping
    public ResponseEntity<Page<Buyer>> getAllBuyers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Buyer> buyers = buyerService.getAllBuyers(pageable);
        
        return ResponseEntity.ok(buyers);
    }
    
    // GET /buyers/{id} - Get a specific buyer
    @GetMapping("/{id}")
    public ResponseEntity<Buyer> getBuyerById(@PathVariable Long id) {
        Optional<Buyer> buyer = buyerService.getBuyerById(id);
        
        if (buyer.isPresent()) {
            return ResponseEntity.ok(buyer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /buyers - Create a buyer
    @PostMapping
    public ResponseEntity<Buyer> createBuyer(@Valid @RequestBody Buyer buyer) {
        try {
            Buyer createdBuyer = buyerService.createBuyer(buyer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBuyer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /buyers/{id} - Update a buyer
    @PutMapping("/{id}")
    public ResponseEntity<Buyer> updateBuyer(@PathVariable Long id, 
                                           @Valid @RequestBody Buyer buyerDetails) {
        try {
            Buyer updatedBuyer = buyerService.updateBuyer(id, buyerDetails);
            return ResponseEntity.ok(updatedBuyer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /buyers/{id} - Delete a buyer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Long id) {
        try {
            buyerService.deleteBuyer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // GET /buyers/search - Search buyers by name
    @GetMapping("/search")
    public ResponseEntity<Page<Buyer>> searchBuyers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Buyer> buyers = buyerService.searchBuyersByName(name, pageable);
        
        return ResponseEntity.ok(buyers);
    }
    
    // GET /buyers/{id}/purchases - Get purchases for a specific buyer
    @GetMapping("/{id}/purchases")
    public ResponseEntity<?> getBuyerPurchases(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (!buyerService.buyerExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
        return ResponseEntity.ok(purchaseService.getPurchasesByBuyerId(id, pageable));
    }
    
    // GET /buyers/{id}/spending - Get total spending for a buyer
    @GetMapping("/{id}/spending")
    public ResponseEntity<?> getBuyerTotalSpending(@PathVariable Long id) {
        if (!buyerService.buyerExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Double totalSpending = purchaseService.getTotalSpendingForBuyer(id);
        return ResponseEntity.ok(new SpendingResponse(totalSpending));
    }
    
    // GET /buyers/with-purchases - Get buyers who have made purchases
    @GetMapping("/with-purchases")
    public ResponseEntity<Page<Buyer>> getBuyersWithPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Buyer> buyers = buyerService.getBuyersWithPurchases(pageable);
        
        return ResponseEntity.ok(buyers);
    }
    
    // Inner class for spending response
    public static class SpendingResponse {
        private Double totalSpending;
        
        public SpendingResponse(Double totalSpending) {
            this.totalSpending = totalSpending;
        }
        
        public Double getTotalSpending() {
            return totalSpending;
        }
        
        public void setTotalSpending(Double totalSpending) {
            this.totalSpending = totalSpending;
        }
    }
}