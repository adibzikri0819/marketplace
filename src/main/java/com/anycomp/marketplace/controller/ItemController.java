package com.anycomp.marketplace.controller;

import com.anycomp.marketplace.entity.Item;
import com.anycomp.marketplace.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {
@Autowired
private ItemService itemService;

// Create a new item
@PostMapping
public ResponseEntity<Item> createItem(@Valid @RequestBody Item item, @RequestParam Long sellerId) {
    try {
        Item createdItem = itemService.createItem(item, sellerId);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}

// Get all items with pagination
@GetMapping
public ResponseEntity<Page<Item>> getAllItems(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getAllItems(pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get item by ID
@GetMapping("/{id}")
public ResponseEntity<Item> getItemById(@PathVariable Long id) {
    Optional<Item> item = itemService.getItemById(id);
    return item.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
              .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

// Get items by seller ID
@GetMapping("/seller/{sellerId}")
public ResponseEntity<Page<Item>> getItemsBySellerId(
        @PathVariable Long sellerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getItemsBySellerId(sellerId, pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Update item
@PutMapping("/{id}")
public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item itemDetails) {
    try {
        Item updatedItem = itemService.updateItem(id, itemDetails);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}

// Delete item
@DeleteMapping("/{id}")
public ResponseEntity<Map<String, String>> deleteItem(@PathVariable Long id) {
    try {
        itemService.deleteItem(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

// Search items by name
@GetMapping("/search")
public ResponseEntity<Page<Item>> searchItemsByName(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.searchItemsByName(name, pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get items by price range
@GetMapping("/price-range")
public ResponseEntity<Page<Item>> getItemsByPriceRange(
        @RequestParam Double minPrice,
        @RequestParam Double maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getItemsByPriceRange(minPrice, maxPrice, pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get available items
@GetMapping("/available")
public ResponseEntity<Page<Item>> getAvailableItems(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getAvailableItems(pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get low stock items
@GetMapping("/low-stock")
public ResponseEntity<Page<Item>> getLowStockItems(
        @RequestParam Integer threshold,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getLowStockItems(threshold, pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get available items by seller
@GetMapping("/seller/{sellerId}/available")
public ResponseEntity<Page<Item>> getAvailableItemsBySellerId(
        @PathVariable Long sellerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getAvailableItemsBySellerId(sellerId, pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get items ordered by price (ascending)
@GetMapping("/price/asc")
public ResponseEntity<Page<Item>> getItemsByPriceAsc(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getItemsByPriceAsc(pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Get items ordered by price (descending)
@GetMapping("/price/desc")
public ResponseEntity<Page<Item>> getItemsByPriceDesc(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Item> items = itemService.getItemsByPriceDesc(pageable);
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// Update item quantity
@PatchMapping("/{id}/quantity")
public ResponseEntity<Item> updateItemQuantity(
        @PathVariable Long id,
        @RequestParam Integer quantity) {
    
    try {
        Item updatedItem = itemService.updateItemQuantity(id, quantity);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}

// Check item availability
@GetMapping("/{id}/availability")
public ResponseEntity<Map<String, Object>> checkItemAvailability(
        @PathVariable Long id,
        @RequestParam Integer requestedQuantity) {
    
    boolean isAvailable = itemService.isItemAvailable(id, requestedQuantity);
    Map<String, Object> response = new HashMap<>();
    response.put("itemId", id);
    response.put("requestedQuantity", requestedQuantity);
    response.put("available", isAvailable);
    
    return new ResponseEntity<>(response, HttpStatus.OK);
}

// Get total items count
@GetMapping("/count/total")
public ResponseEntity<Map<String, Long>> getTotalItemsCount() {
    long count = itemService.getTotalItemsCount();
    Map<String, Long> response = new HashMap<>();
    response.put("totalItems", count);
    return new ResponseEntity<>(response, HttpStatus.OK);
}

// Get available items count
@GetMapping("/count/available")
public ResponseEntity<Map<String, Long>> getAvailableItemsCount() {
    long count = itemService.getAvailableItemsCount();
    Map<String, Long> response = new HashMap<>();
    response.put("availableItems", count);
    return new ResponseEntity<>(response, HttpStatus.OK);
}

// Check if item exists
@GetMapping("/{id}/exists")
public ResponseEntity<Map<String, Object>> itemExists(@PathVariable Long id) {
    boolean exists = itemService.itemExists(id);
    Map<String, Object> response = new HashMap<>();
    response.put("itemId", id);
    response.put("exists", exists);
    return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
