package com.devforge.project.repository;

import com.devforge.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Project> search(String query, Pageable pageable);
}
