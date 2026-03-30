package com.devforge.project.controller;

import com.devforge.project.model.Project;
import com.devforge.project.service.ProjectService;
import com.devforge.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<Project>> listAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Project>> search(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.search(q, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Project>> getByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.findByOwner(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<Project> create(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String githubUrl,
            @RequestParam(required = false) String liveUrl,
            @RequestParam(required = false) List<String> tags,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(name, description, githubUrl, liveUrl, tags, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String githubUrl,
            @RequestParam(required = false) String liveUrl,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(projectService.updateProject(id, name, description, githubUrl, liveUrl, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        projectService.deleteProject(id, user);
        return ResponseEntity.noContent().build();
    }
}
