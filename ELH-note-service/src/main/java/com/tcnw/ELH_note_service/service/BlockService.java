package com.tcnw.ELH_note_service.service;

import com.tcnw.ELH_note_service.dto.BlockDTO;
import com.tcnw.ELH_note_service.dto.CreateBlockRequest;
import com.tcnw.ELH_note_service.dto.UpdateBlockRequest;
import com.tcnw.ELH_note_service.entity.Block;
import com.tcnw.ELH_note_service.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final ModelMapper modelMapper;

    // 1. Lấy danh sách Root (Sidebar)
    public List<BlockDTO> getRootBlocks(String userId) {
        List<Block> blocks = blockRepository.findRootBlocks(userId);
        return blocks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 2. Lấy nội dung chi tiết (Children của 1 Page/Folder)
    public List<BlockDTO> getChildren(UUID parentId) {
        List<Block> blocks = blockRepository.findAllByParentIdAndArchivedFalseOrderBySortOrderAsc(parentId);
        return blocks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 3. Tạo Block mới (Xử lý sort_order)
    @Transactional
    public BlockDTO createBlock(String userId, CreateBlockRequest request) {
        Block block = new Block();
        block.setUserId(userId);
        block.setType(request.getType());
        block.setProperties(request.getProperties());
        block.setParentId(request.getParentId());

        // Logic tính toán Sort Order đơn giản: Chèn xuống cuối cùng
        Block lastBlock = blockRepository.findFirstByParentIdOrderBySortOrderDesc(request.getParentId());
        double newSortOrder = (lastBlock != null && lastBlock.getSortOrder() != null)
                ? lastBlock.getSortOrder() + 1000.0
                : 1000.0;

        block.setSortOrder(newSortOrder);

        return convertToDTO(blockRepository.save(block));
    }

    // 4. Cập nhật (Rename, Update Content, Move)
    @Transactional
    public BlockDTO updateBlock(String userId, UUID blockId, UpdateBlockRequest request) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found"));

        if (!block.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getProperties() != null) {
            block.setProperties(request.getProperties());
        }

        if (request.getParentId() != null) {
            block.setParentId(request.getParentId());
        }

        if (request.getSortOrder() != null) {
            block.setSortOrder(request.getSortOrder());
        }

        return convertToDTO(blockRepository.save(block));
    }

    @Transactional
    public void deleteBlock(String userId, UUID blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found"));
        block.setArchived(true);
        blockRepository.save(block);
    }

    // Helper: Map Entity -> DTO
    private BlockDTO convertToDTO(Block block) {
        BlockDTO dto = modelMapper.map(block, BlockDTO.class);
        return dto;
    }
}