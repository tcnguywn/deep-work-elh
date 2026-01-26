package com.tcnw.ELH_time_tracking_service.repositories;

import com.tcnw.ELH_time_tracking_service.entities.models.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    // 1. Lấy danh sách nhật ký trong khoảng thời gian (VD: Hôm nay, Tuần này)
    // Dùng để vẽ biểu đồ chi tiết hoặc hiển thị list "Recent Activity"
    List<TimeEntry> findAllByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
            String userId,
            LocalDateTime start,
            LocalDateTime end
    );

    // 2. Tính TỔNG THỜI GIAN làm việc
    // Trả về null nếu không có dữ liệu -> Service cần xử lý null
    @Query("SELECT SUM(t.durationSeconds) FROM TimeEntry t " +
            "WHERE t.userId = :userId " +
            "AND t.startTime BETWEEN :start AND :end")
    Long getTotalDurationByRange(
            @Param("userId") String userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 3. Tính tổng thời gian Deep Work (Cho cái toggle "Deep Focus")
    @Query("SELECT SUM(t.durationSeconds) FROM TimeEntry t " +
            "WHERE t.userId = :userId " +
            "AND t.deepWork = true " +
            "AND t.startTime BETWEEN :start AND :end")
    Long getDeepWorkDurationByRange(
            @Param("userId") String userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 4. Đếm số ngày làm việc (Active Days)
    @Query(
            value = """
                SELECT COUNT(DISTINCT DATE(t.start_time))
                FROM time_entry t
                WHERE t.user_id = :userId
                AND t.start_time BETWEEN :start AND :end
            """,
            nativeQuery = true
    )
    Long countActiveDays(
            @Param("userId") String userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}