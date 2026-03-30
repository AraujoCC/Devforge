package com.devforge.post.service;

import com.devforge.post.model.Post;
import com.devforge.post.model.PostLike;
import com.devforge.post.repository.PostRepository;
import com.devforge.user.model.User;
import com.devforge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Post createPost(String content, User author) {
        var post = Post.builder()
                .content(content)
                .author(author)
                .build();
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable);
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
    }

    @Transactional
    public Post toggleLike(Long postId, User user) {
        var post = findById(postId);

        boolean alreadyLiked = postRepository.existsLikeByPostIdAndUserId(postId, user.getId());

        if (alreadyLiked) {
            post.getLikes().removeIf(like -> like.getUser().getId().equals(user.getId()));
        } else {
            var like = PostLike.builder().post(post).user(user).build();
            post.getLikes().add(like);
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id, User requestingUser) {
        var post = findById(id);
        boolean isOwner = post.getAuthor().getId().equals(requestingUser.getId());
        boolean isAdmin = requestingUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You are not allowed to delete this post.");
        }
        postRepository.delete(post);
    }
}
