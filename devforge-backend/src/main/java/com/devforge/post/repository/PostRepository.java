package com.devforge.post.repository;

import com.devforge.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId ORDER BY p.createdAt DESC")
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    boolean existsLikeByPostIdAndUserId(Long postId, Long userId);
}
