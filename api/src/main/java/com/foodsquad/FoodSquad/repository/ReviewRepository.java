package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMenuItemId(Long menuItemId);
    List<Review> findByUserId(String userId);
    List<Review> findByUserIdOrderByCreatedOnDesc(String userId, Pageable pageable);
    long countByMenuItemId(Long menuItemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.menuItem.id = :menuItemId")
    Double findAverageRatingByMenuItemId(Long menuItemId);
}
