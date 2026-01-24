package com.tcnw.ELH_task_service.services.tag;

import com.tcnw.ELH_task_service.dtos.TagDTO;
import com.tcnw.ELH_task_service.entities.tag.Tag;
import com.tcnw.ELH_task_service.repositories.tag.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public List<TagDTO> getAllTags(String userId) {
        return tagRepository.findAllByUserId(userId).stream()
                .map(tag -> modelMapper.map(tag, TagDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public TagDTO createTag(String userId, TagDTO dto) {
        Tag tag = modelMapper.map(dto, Tag.class);
        tag.setUserId(userId);
        return modelMapper.map(tagRepository.save(tag), TagDTO.class);
    }

    @Transactional
    public void deleteTag(String userId, Long tagId) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        tagRepository.delete(tag);
    }
}