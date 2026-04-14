package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByClientEmail(String email, Pageable pageable);
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
    Order findOrderById(Long id);
    Page<Order> findAllByEmployeeEmail(String email, Pageable pageable);
    List<Order> findByEmployeeIsNull();
    void deleteAllByClientId(Long id);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' " +
            "AND (:search IS NULL OR LOWER(o.client.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findPendingWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status != 'PENDING' " +
            "AND (:search IS NULL OR LOWER(o.client.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:onlyMy = false OR o.employee.email = :employeeEmail)")
    Page<Order> findOrderHistoryWithSearch(@Param("search") String search,
                                           @Param("onlyMy") boolean onlyMy,
                                           @Param("employeeEmail") String employeeEmail,
                                           Pageable pageable);
}
