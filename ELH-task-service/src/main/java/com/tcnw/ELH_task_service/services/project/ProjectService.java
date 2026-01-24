package com.tcnw.ELH_task_service.services.project;

import com.tcnw.ELH_task_service.dtos.ProjectDTO;
import com.tcnw.ELH_task_service.entities.project.Project;
import com.tcnw.ELH_task_service.repositories.project.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public Page<ProjectDTO> getProjects(String userId, Pageable pageable) {
        return projectRepository.findAllByUserId(userId, pageable)
                .map(project -> modelMapper.map(project, ProjectDTO.class));
    }

    public ProjectDTO getProjectById(String userId, Long projectId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));
        return modelMapper.map(project, ProjectDTO.class);
    }

    @Transactional
    public ProjectDTO createProject(String userId, ProjectDTO dto) {
        Project project = modelMapper.map(dto, Project.class);
        project.setUserId(userId); // Gán User ID từ token

        Project saved = projectRepository.save(project);
        return modelMapper.map(saved, ProjectDTO.class);
    }

    @Transactional
    public ProjectDTO updateProject(String userId, Long projectId, ProjectDTO dto) {
        Project existingProject = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        existingProject.setName(dto.getName());
        existingProject.setDescription(dto.getDescription());
        existingProject.setColor(dto.getColor());

        return modelMapper.map(projectRepository.save(existingProject), ProjectDTO.class);
    }

    @Transactional
    public void deleteProject(String userId, Long projectId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        projectRepository.delete(project);
    }
}
