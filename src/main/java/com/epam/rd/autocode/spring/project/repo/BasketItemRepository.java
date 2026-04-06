package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
}
