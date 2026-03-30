package com.devforge.project.service;

import com.devforge.project.model.Project;
import com.devforge.project.model.ProjectTag;
import com.devforge.project.repository.ProjectRepository;
import com.devforge.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Project createProject(String name, String description,
                                  String githubUrl, String liveUrl,
                                  List<String> tagNames, User owner) {
        var project = Project.builder()
                .name(name)
                .description(description)
                .githubUrl(githubUrl)
                .liveUrl(liveUrl)
                .owner(owner)
                .build();

        if (tagNames != null) {
            tagNames.forEach(t -> project.getTags().add(
                ProjectTag.builder().name(t).project(project).build()
            ));
        }

        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Project> findByOwner(Long ownerId, Pageable pageable) {
        return projectRepository.findByOwnerId(ownerId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Project> search(String query, Pageable pageable) {
        return projectRepository.search(query, pageable);
    }

    @Transactional
    public Project updateProject(Long id, String name, String description,
                                  String githubUrl, String liveUrl, User requestingUser) {
        var project = findById(id);
        ensureOwnership(project, requestingUser);

        if (name != null)        project.setName(name);
        if (description != null) project.setDescription(description);
        if (githubUrl != null)   project.setGithubUrl(githubUrl);
        if (liveUrl != null)     project.setLiveUrl(liveUrl);

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id, User requestingUser) {
        var project = findById(id);
        ensureOwnership(project, requestingUser);
        projectRepository.delete(project);
    }

    private void ensureOwnership(Project project, User user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!project.getOwner().getId().equals(user.getId()) && !isAdmin) {
            throw new SecurityException("You are not the owner of this project.");
        }
    }
}
