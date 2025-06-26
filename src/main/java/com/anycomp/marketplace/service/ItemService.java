package com.anycomp.marketplace.service;

import com.anycomp.marketplace.entity.Item;
import com.anycomp.marketplace.entity.Seller;
import com.anycomp.marketplace.repository.ItemRepository;
import com.anycomp.marketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private SellerRepository sellerRepository;
    
    // Create a new item
    public Item createItem(Item item, Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));
        
        item.setSeller(seller);
        return itemRepository.save(item);
    }
    
    // Get all items with pagination
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
    
    // Get item by ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
    
    // Get items by seller ID
    public Page<Item> getItemsBySellerId(Long sellerId, Pageable pageable) {
        return itemRepository.findBySellerId(sellerId, pageable);
    }
    
    // Update item
    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setPrice(itemDetails.getPrice());
        item.setQuantity(itemDetails.getQuantity());
        
        return itemRepository.save(item);
    }
    
    
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        
        if (!item.getPurchases().isEmpty()) {
            throw new RuntimeException("Cannot delete item with existing purchases");
        }
        
        itemRepository.delete(item);
    }
    
    
    public Page<Item> searchItemsByName(String name, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    
    public Page<Item> getItemsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return itemRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }
    
    
    public Page<Item> getAvailableItems(Pageable pageable) {
        return itemRepository.findAvailableItems(pageable);
    }
    
    
    public Page<Item> getLowStockItems(Integer threshold, Pageable pageable) {
        return itemRepository.findLowStockItems(threshold, pageable);
    }
    
    
    public Page<Item> getAvailableItemsBySellerId(Long sellerId, Pageable pageable) {
        return itemRepository.findAvailableItemsBySellerId(sellerId, pageable);
    }
    
    
    public Page<Item> getItemsByPriceAsc(Pageable pageable) {
        return itemRepository.findAllByOrderByPriceAsc(pageable);
    }
    
    
    public Page<Item> getItemsByPriceDesc(Pageable pageable) {
        return itemRepository.findAllByOrderByPriceDesc(pageable);
    }
    
    
    public Item updateItemQuantity(Long id, Integer newQuantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        item.setQuantity(newQuantity);
        return itemRepository.save(item);
    }
    
    
    public boolean isItemAvailable(Long itemId, Integer requestedQuantity) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return false;
        }
        Item item = itemOpt.get();
        return item.getQuantity() >= requestedQuantity;
    }
    
    
    public long getTotalItemsCount() {
        return itemRepository.countTotalItems();
    }
    
    
    public long getAvailableItemsCount() {
        return itemRepository.countAvailableItems();
    }
    
    
    public boolean itemExists(Long id) {
        return itemRepository.existsById(id);
    }
}