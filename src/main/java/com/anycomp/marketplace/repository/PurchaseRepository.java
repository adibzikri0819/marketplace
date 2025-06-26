package com.anycomp.marketplace.repository;

import com.anycomp.marketplace.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    
    Page<Purchase> findByBuyerId(Long buyerId, Pageable pageable);
    
    
    Page<Purchase> findByItemId(Long itemId, Pageable pageable);
    
    
    @Query("SELECT p FROM Purchase p WHERE p.item.seller.id = :sellerId")
    Page<Purchase> findByItemSellerId(@Param("sellerId") Long sellerId, Pageable pageable);
    
    
    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    Page<Purchase> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate, 
                                           Pageable pageable);
    
    
    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate >= :fromDate ORDER BY p.purchaseDate DESC")
    Page<Purchase> findRecentPurchases(@Param("fromDate") LocalDateTime fromDate, Pageable pageable);
    
    
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) FROM Purchase p WHERE p.item.seller.id = :sellerId")
    Double getTotalRevenueForSeller(@Param("sellerId") Long sellerId);
    
    
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) FROM Purchase p WHERE p.buyer.id = :buyerId")
    Double getTotalSpendingForBuyer(@Param("buyerId") Long buyerId);
    
    
    @Query("SELECT p.item.id, p.item.name, SUM(p.quantity) as totalQuantity " +
           "FROM Purchase p " +
           "GROUP BY p.item.id, p.item.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> getMostPurchasedItems();
    
    
    @Query("SELECT COUNT(p) FROM Purchase p")
    long countTotalPurchases();
}
