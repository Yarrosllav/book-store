package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByName(String name);
    Boolean existsByName(String name);
    Page<Book> findAll(Pageable pageable);

    @Query("""
    SELECT b FROM Book b WHERE b.isAvailable = true AND
    (:search IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))
                     OR LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:genre IS NULL OR b.genre = :genre)
    AND (:ageGroup IS NULL OR b.ageGroup = :ageGroup)
    AND (:minPrice IS NULL OR b.price >= :minPrice)
    AND (:maxPrice IS NULL OR b.price <= :maxPrice)
    """)
    Page<Book> findWithFilters(
            @Param("search") String search,
            @Param("genre") Genre genre,
            @Param("ageGroup") AgeGroup ageGroup,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

}
