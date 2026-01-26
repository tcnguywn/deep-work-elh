-- 1. Bảng Cấu hình (Settings)
-- Lưu sở thích của user, hỗ trợ cả Pomodoro và Flowmodoro
CREATE TABLE pomodoro_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE, -- User ID từ Keycloak

    -- Cấu hình Pomodoro truyền thống
    work_duration_minutes INT DEFAULT 25,
    short_break_minutes INT DEFAULT 5,
    long_break_minutes INT DEFAULT 15,
    long_break_interval INT DEFAULT 4,

    -- Cấu hình Flowmodoro (Mới)
    -- Tỷ lệ chia thời gian nghỉ. VD: 5 nghĩa là (Thời gian làm / 5)
    flowmodoro_denominator INT DEFAULT 5,

    -- Tự động
    auto_start_break BOOLEAN DEFAULT FALSE,
    auto_start_pomodoro BOOLEAN DEFAULT FALSE,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Phiên làm việc (Sessions)
-- Lưu chi tiết từng lần bấm giờ (Timer)
CREATE TABLE pomodoro_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    task_id BIGINT,                      -- Link sang Task Service (có thể null)

    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,                  -- Null nếu đang chạy
    duration_seconds BIGINT DEFAULT 0,   -- Thời gian thực tế đếm được

    -- Status:
    -- RUNNING: Đang đếm
    -- PAUSED: Tạm dừng
    -- COMPLETED: Hoàn thành (Hết giờ countdown hoặc Stop flowmodoro)
    -- STOPPED: Dừng sớm (khi chưa hết giờ countdown)
    status VARCHAR(20) DEFAULT 'RUNNING',

    -- Mode: WORK (Làm), SHORT_BREAK (Nghỉ ngắn), LONG_BREAK (Nghỉ dài)
    mode VARCHAR(20) DEFAULT 'WORK',

    -- Style (Mới): COUNTDOWN (Pomodoro chuẩn), STOPWATCH (Flowmodoro)
    style VARCHAR(20) DEFAULT 'COUNTDOWN',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng Nhật ký thời gian (Time Entries)
-- Dùng để vẽ biểu đồ Dashboard, tách biệt với logic đếm giờ
CREATE TABLE time_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,

    task_id BIGINT,                      -- Link sang Task Service
    project_id BIGINT,                   -- Link sang Project (để thống kê theo dự án, null = No Project)

    description TEXT,                    -- Ghi chú (nếu nhập tay)

    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration_seconds BIGINT NOT NULL,    -- Tổng thời gian (giây)

    -- Phân loại
    is_manual BOOLEAN DEFAULT FALSE,     -- True: Nhập tay, False: Sync từ Pomodoro
    is_deep_work BOOLEAN DEFAULT TRUE,   -- Hỗ trợ toggle "Deep Focus" trên Dashboard

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- INDEX để tối ưu hiệu năng truy vấn (Bắt buộc cho Dashboard)
-- 1. Tìm phiên đang chạy nhanh chóng
CREATE INDEX idx_pomodoro_sessions_user_status ON pomodoro_sessions(user_id, status);

-- 2. Lấy dữ liệu vẽ biểu đồ (lọc theo user và khoảng thời gian)
CREATE INDEX idx_time_entries_user_date ON time_entries(user_id, start_time);

-- 3. Thống kê theo dự án (Pie chart)
CREATE INDEX idx_time_entries_project ON time_entries(project_id);