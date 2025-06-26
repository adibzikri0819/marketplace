package com.anycomp.marketplace.service;

import com.anycomp.marketplace.entity.Buyer;
import com.anycomp.marketplace.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class BuyerService {
    
    @Autowired
    private BuyerRepository buyerRepository;
    
    
    public Buyer createBuyer(Buyer buyer) {
        
        if (buyerRepository.existsByEmail(buyer.getEmail())) {
            throw new RuntimeException("Buyer with email " + buyer.getEmail() + " already exists");
        }
        return buyerRepository.save(buyer);
    }
    
    
    public Page<Buyer> getAllBuyers(Pageable pageable) {
        return buyerRepository.findAll(pageable);
    }
    
    
    public Optional<Buyer> getBuyerById(Long id) {
        return buyerRepository.findById(id);
    }
    
    
    public Optional<Buyer> getBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }
    
    
    public Buyer updateBuyer(Long id, Buyer buyerDetails) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + id));
        
        
        if (!buyer.getEmail().equals(buyerDetails.getEmail()) && 
            buyerRepository.existsByEmail(buyerDetails.getEmail())) {
            throw new RuntimeException("Email " + buyerDetails.getEmail() + " is already taken");
        }
        
        buyer.setName(buyerDetails.getName());
        buyer.setEmail(buyerDetails.getEmail());
        
        return buyerRepository.save(buyer);
    }
    
    
    public void deleteBuyer(Long id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + id));
        
        
        if (!buyer.getPurchasedItems().isEmpty()) {
            throw new RuntimeException("Cannot delete buyer with existing purchases");
        }
        
        buyerRepository.delete(buyer);
    }
    
    
    public Page<Buyer> searchBuyersByName(String name, Pageable pageable) {
        return buyerRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    
    public Page<Buyer> getBuyersWithPurchases(Pageable pageable) {
        return buyerRepository.findBuyersWithPurchases(pageable);
    }
    
    
    public long getTotalBuyersCount() {
        return buyerRepository.countTotalBuyers();
    }
    
    
    public boolean buyerExists(Long id) {
        return buyerRepository.existsById(id);
    }
}