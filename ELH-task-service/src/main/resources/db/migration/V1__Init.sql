-- 1. Bảng Projects (Danh mục/Dự án để nhóm nhiệm vụ)
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL, -- UUID từ Keycloak/User Service
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(7), -- Mã màu Hex (ví dụ: #FF0000) để hiển thị UI
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Tasks (Nhiệm vụ chính)
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL, -- Quan trọng: Để filter task của user nào
    project_id BIGINT,            -- Link tới dự án (Optional)

    title VARCHAR(255) NOT NULL,  -- [Tên nhiệm vụ]
    description TEXT,             -- [Mô tả chi tiết]

    -- [Mức độ ưu tiên: cao, trung bình, thấp]
    -- Giá trị đề xuất: LOW, MEDIUM, HIGH
    priority VARCHAR(20) DEFAULT 'MEDIUM',

    -- [Trạng thái: Chưa làm / Đang làm / Hoàn thành]
    -- Giá trị đề xuất: TODO, IN_PROGRESS, DONE
    status VARCHAR(20) DEFAULT 'TODO',

    due_date TIMESTAMP,           -- [Thời hạn hoàn thành (deadline)]
    estimated_time_minutes INT,   -- [Dự kiến thời gian cần thiết - tính bằng phút]

    completed_at TIMESTAMP,       -- Thời điểm hoàn thành thực tế
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL
);

-- 3. Bảng Tags (Nhãn dán)
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7)
);

-- 4. Bảng liên kết Task - Tag (Many-to-Many)
CREATE TABLE task_tags (
    task_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, tag_id),

    CONSTRAINT fk_tt_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_tt_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 5. Đánh Index (Tối ưu hóa truy vấn)
-- Vì đây là hệ thống Multi-tenant (mỗi user dữ liệu riêng), index user_id là BẮT BUỘC.
CREATE INDEX idx_projects_user ON projects(user_id);
CREATE INDEX idx_tasks_user ON tasks(user_id);
CREATE INDEX idx_tags_user ON tags(user_id);

-- Index hỗ trợ tính năng "Sắp xếp nhiệm vụ theo: ưu tiên, deadline" [Source: 29]
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);