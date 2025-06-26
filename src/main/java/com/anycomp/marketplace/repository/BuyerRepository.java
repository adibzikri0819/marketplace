package com.anycomp.marketplace.repository;

import com.anycomp.marketplace.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    
    // Find buyer by email
    Optional<Buyer> findByEmail(String email);
    
    // Check if buyer exists by email
    boolean existsByEmail(String email);
    
    // Find buyers by name containing (case insensitive)
    @Query("SELECT b FROM Buyer b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Buyer> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    // Find all buyers with pagination
    Page<Buyer> findAll(Pageable pageable);
    
    // Find buyers who have made purchases
    @Query("SELECT DISTINCT b FROM Buyer b JOIN b.purchasedItems p")
    Page<Buyer> findBuyersWithPurchases(Pageable pageable);
    
    // Count total number of buyers
    @Query("SELECT COUNT(b) FROM Buyer b")
    long countTotalBuyers();
}
