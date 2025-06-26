package com.anycomp.marketplace.service;

import com.anycomp.marketplace.entity.Seller;
import com.anycomp.marketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class SellerService {
    
    @Autowired
    private SellerRepository sellerRepository;
    
    
    public Seller createSeller(Seller seller) {
        
        if (sellerRepository.existsByEmail(seller.getEmail())) {
            throw new RuntimeException("Seller with email " + seller.getEmail() + " already exists");
        }
        return sellerRepository.save(seller);
    }
    
    
    public Page<Seller> getAllSellers(Pageable pageable) {
        return sellerRepository.findAll(pageable);
    }
    
    
    public Optional<Seller> getSellerById(Long id) {
        return sellerRepository.findById(id);
    }
    
    
    public Optional<Seller> getSellerByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }
    
    
    public Seller updateSeller(Long id, Seller sellerDetails) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        
        
        if (!seller.getEmail().equals(sellerDetails.getEmail()) && 
            sellerRepository.existsByEmail(sellerDetails.getEmail())) {
            throw new RuntimeException("Email " + sellerDetails.getEmail() + " is already taken");
        }
        
        seller.setName(sellerDetails.getName());
        seller.setEmail(sellerDetails.getEmail());
        
        return sellerRepository.save(seller);
    }
    
    
    public void deleteSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        
        
        if (!seller.getItems().isEmpty()) {
            throw new RuntimeException("Cannot delete seller with existing items");
        }
        
        sellerRepository.delete(seller);
    }
    
    
    public Page<Seller> searchSellersByName(String name, Pageable pageable) {
        return sellerRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    
    public Page<Seller> getSellersWithItems(Pageable pageable) {
        return sellerRepository.findSellersWithItems(pageable);
    }
    
    
    public Page<Seller> getSellersWithSales(Pageable pageable) {
        return sellerRepository.findSellersWithSales(pageable);
    }
    
    
    public long getTotalSellersCount() {
        return sellerRepository.countTotalSellers();
    }
    
    
    public boolean sellerExists(Long id) {
        return sellerRepository.existsById(id);
    }
}