package me.kmj.gather.repository;

import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInfoRepository extends JpaRepository<GroupInfo, Long> {
    
    // 페이징 처리된 그룹 목록 조회
    Page<GroupInfo> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 특정 사용자가 생성한 그룹 목록 조회
    Page<GroupInfo> findByCreatedByOrderByCreatedAtDesc(Member createdBy, Pageable pageable);
    
    // 그룹명으로 검색
    @Query("SELECT g FROM GroupInfo g WHERE g.name LIKE %:keyword% ORDER BY g.createdAt DESC")
    Page<GroupInfo> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
} 