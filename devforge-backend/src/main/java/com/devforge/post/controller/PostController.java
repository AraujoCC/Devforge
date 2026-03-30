package com.devforge.post.controller;

import com.devforge.post.model.Post;
import com.devforge.post.service.PostService;
import com.devforge.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<Post>> getFeed(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getFeed(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Post>> getByAuthor(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByAuthor(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody String content,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(content, user));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Post> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(postService.toggleLike(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }
}
