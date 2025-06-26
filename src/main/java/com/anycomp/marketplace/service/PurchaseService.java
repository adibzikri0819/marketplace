package com.anycomp.marketplace.service;

import com.anycomp.marketplace.entity.Buyer;
import com.anycomp.marketplace.entity.Item;
import com.anycomp.marketplace.entity.Purchase;
import com.anycomp.marketplace.repository.BuyerRepository;
import com.anycomp.marketplace.repository.ItemRepository;
import com.anycomp.marketplace.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private BuyerRepository buyerRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    
    public Purchase buyItem(Long buyerId, Long itemId, Integer quantity) {
        
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + buyerId));
        
        
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        
        
        if (item.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getQuantity() + 
                                     ", Requested: " + quantity);
        }
        
        
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }
        
        
        item.setQuantity(item.getQuantity() - quantity);
        itemRepository.save(item);
        
        
        Purchase purchase = new Purchase(buyer, item, quantity);
        purchase = purchaseRepository.save(purchase);
        
        return purchase;
    }
    
    
    public Page<Purchase> getAllPurchases(Pageable pageable) {
        return purchaseRepository.findAll(pageable);
    }
    
    
    public Optional<Purchase> getPurchaseById(Long id) {
        return purchaseRepository.findById(id);
    }
    
   
    public Page<Purchase> getPurchasesByBuyerId(Long buyerId, Pageable pageable) {
        return purchaseRepository.findByBuyerId(buyerId, pageable);
    }
    
    
    public Page<Purchase> getPurchasesByItemId(Long itemId, Pageable pageable) {
        return purchaseRepository.findByItemId(itemId, pageable);
    }
    
    
    public Page<Purchase> getPurchasesBySellerId(Long sellerId, Pageable pageable) {
        return purchaseRepository.findByItemSellerId(sellerId, pageable);
    }
    
    
    public Page<Purchase> getPurchasesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return purchaseRepository.findByPurchaseDateBetween(startDate, endDate, pageable);
    }
    
    
    public Page<Purchase> getRecentPurchases(int days, Pageable pageable) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return purchaseRepository.findRecentPurchases(fromDate, pageable);
    }
    
    
    public Double getTotalRevenueForSeller(Long sellerId) {
        return purchaseRepository.getTotalRevenueForSeller(sellerId);
    }
    
    
    public Double getTotalSpendingForBuyer(Long buyerId) {
        return purchaseRepository.getTotalSpendingForBuyer(buyerId);
    }
    
    
    public List<Object[]> getMostPurchasedItems() {
        return purchaseRepository.getMostPurchasedItems();
    }
    
    
    public long getTotalPurchasesCount() {
        return purchaseRepository.countTotalPurchases();
    }
    
    
    public void cancelPurchase(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + purchaseId));
        
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        if (purchase.getPurchaseDate().isBefore(cutoffTime)) {
            throw new RuntimeException("Cannot cancel purchase older than 24 hours");
        }
        
        
        Item item = purchase.getItem();
        item.setQuantity(item.getQuantity() + purchase.getQuantity());
        itemRepository.save(item);
        
        
        purchaseRepository.delete(purchase);
    }
    
    
    public boolean validatePurchaseRequest(Long buyerId, Long itemId, Integer quantity) {
        
        if (!buyerRepository.existsById(buyerId)) {
            return false;
        }
        
        
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return false;
        }
        
        Item item = itemOpt.get();
        return item.getQuantity() >= quantity && quantity > 0;
    }
}
