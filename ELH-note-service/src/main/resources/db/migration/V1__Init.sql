-- Kích hoạt extension để sinh UUID ngẫu nhiên
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Bảng Blocks: Chứa tất cả Folder, Page, Paragraph, Todo...
CREATE TABLE blocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- ID dạng UUID để dễ sync/move
    user_id VARCHAR(36) NOT NULL,
    -- Cấu trúc cây: Block này nằm trong Block nào?
    -- Nếu là Root Page/Folder thì parent_id = NULL
    parent_id UUID,
    -- Loại block: 'page', 'folder', 'paragraph', 'heading_1', 'to_do', 'image'...
    type VARCHAR(50) NOT NULL,
    -- QUAN TRỌNG: Cột JSONB lưu trữ dữ liệu động
    -- VD Page: { "title": "Học Java", "cover_image": "..." }
    -- VD Todo: { "title": "Mua rau", "checked": true, "color": "red" }
    properties JSONB,
    -- Nội dung con (nếu cần cache IDs con) - Ở đây ta dùng quan hệ parent_id là đủ
    -- content JSONB,
    -- Sắp xếp: Dùng số thực để dễ chèn vào giữa (Fractional Indexing)
    -- VD: Muốn chèn giữa 1.0 và 2.0 thì đặt là 1.5
    sort_order DOUBLE PRECISION DEFAULT 1000.0,
    -- Meta data
    created_by VARCHAR(36),
    last_edited_by VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Soft delete (Thùng rác)
    is_archived BOOLEAN DEFAULT FALSE,
    -- Ràng buộc khóa ngoại đệ quy (Block con trỏ về Block cha)
    CONSTRAINT fk_block_parent FOREIGN KEY (parent_id) REFERENCES blocks(id) ON DELETE CASCADE
);

-- Indexes để tối ưu hiệu năng
-- 1. Tìm nhanh tất cả block con của 1 page (Load nội dung Page)
CREATE INDEX idx_blocks_parent ON blocks(parent_id);

-- 2. Tìm nhanh block của user (Load Sidebar)
CREATE INDEX idx_blocks_user ON blocks(user_id);

-- 3. Full-text search trong JSON (Tìm kiếm nội dung note)
CREATE INDEX idx_blocks_properties ON blocks USING gin (properties);