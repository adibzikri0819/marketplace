package com.anycomp.marketplace.repository;

import com.anycomp.marketplace.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    
    Page<Item> findBySellerId(Long sellerId, Pageable pageable);
    
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Item> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    Page<Item> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    
    @Query("SELECT i FROM Item i WHERE i.quantity > 0")
    Page<Item> findAvailableItems(Pageable pageable);
    
    
    @Query("SELECT i FROM Item i WHERE i.quantity <= :threshold")
    Page<Item> findLowStockItems(@Param("threshold") Integer threshold, Pageable pageable);
    
    
    @Query("SELECT i FROM Item i WHERE i.seller.id = :sellerId AND i.quantity > 0")
    Page<Item> findAvailableItemsBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
    
    
    Page<Item> findAllByOrderByPriceAsc(Pageable pageable);
    
    
    Page<Item> findAllByOrderByPriceDesc(Pageable pageable);
    
    
    @Query("SELECT COUNT(i) FROM Item i")
    long countTotalItems();
    
    
    @Query("SELECT COUNT(i) FROM Item i WHERE i.quantity > 0")
    long countAvailableItems();
}
