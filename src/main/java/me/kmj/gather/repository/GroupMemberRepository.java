package me.kmj.gather.repository;

import me.kmj.gather.entity.GroupMember;
import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.entity.Member;
import me.kmj.gather.entity.GroupMember.GroupMemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    // 특정 그룹의 멤버 목록 조회
    Page<GroupMember> findByGroupOrderByRequestedAtDesc(GroupInfo group, Pageable pageable);
    
    // 특정 사용자의 그룹 가입 신청 목록 조회
    Page<GroupMember> findByMemberOrderByRequestedAtDesc(Member member, Pageable pageable);
    
    // 특정 그룹의 특정 상태 멤버 목록 조회
    List<GroupMember> findByGroupAndStatus(GroupInfo group, GroupMemberStatus status);
    
    // 특정 사용자가 특정 그룹에 이미 신청했는지 확인
    Optional<GroupMember> findByGroupAndMember(GroupInfo group, Member member);
    
    // 특정 그룹의 승인된 멤버 수 조회
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group = :group AND gm.status = 'APPROVED'")
    long countApprovedMembers(@Param("group") GroupInfo group);
    
    // 특정 그룹의 대기중인 가입 신청 수 조회
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group = :group AND gm.status = 'PENDING'")
    long countPendingMembers(@Param("group") GroupInfo group);
} 