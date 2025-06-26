package com.anycomp.marketplace.repository;

import com.anycomp.marketplace.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    
    
    Optional<Seller> findByEmail(String email);
    
    
    boolean existsByEmail(String email);
    
    
    @Query("SELECT s FROM Seller s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Seller> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    
    Page<Seller> findAll(Pageable pageable);
    
    
    @Query("SELECT DISTINCT s FROM Seller s JOIN s.items i")
    Page<Seller> findSellersWithItems(Pageable pageable);
    
    
    @Query("SELECT DISTINCT s FROM Seller s JOIN s.items i JOIN i.purchases p")
    Page<Seller> findSellersWithSales(Pageable pageable);
    
    
    @Query("SELECT COUNT(s) FROM Seller s")
    long countTotalSellers();
}
