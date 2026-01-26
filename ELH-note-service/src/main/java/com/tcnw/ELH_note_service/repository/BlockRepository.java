package com.tcnw.ELH_note_service.repository;

import com.tcnw.ELH_note_service.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockRepository extends JpaRepository<Block, UUID> {

    // 1. Lấy các block gốc (Root) để hiển thị Sidebar ban đầu
    // Chỉ lấy Folder và Page, sắp xếp theo thứ tự
    @Query("SELECT b FROM Block b WHERE b.userId = :userId AND b.parentId IS NULL AND b.archived = false ORDER BY b.sortOrder ASC")
    List<Block> findRootBlocks(String userId);

    // 2. Lấy danh sách con của 1 Block (Load nội dung trang hoặc mở rộng folder)
    List<Block> findAllByParentIdAndArchivedFalseOrderBySortOrderAsc(UUID parentId);

    // 3. Tìm block lớn nhất để tính sort_order khi thêm mới (để chèn xuống cuối)
    Block findFirstByParentIdOrderBySortOrderDesc(UUID parentId);
}