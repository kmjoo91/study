package me.kmj.gather.controller;

import me.kmj.gather.entity.GroupMember;
import me.kmj.gather.entity.GroupMember.GroupMemberStatus;
import me.kmj.gather.service.GroupMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupMemberController.class);
    
    private final GroupMemberService groupMemberService;
    
    @Autowired
    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }
    
    // 그룹 가입 신청
    @PostMapping("/request")
    public ResponseEntity<GroupMember> requestGroupMembership(@RequestParam Long groupId, 
                                                           @RequestParam String memberId) {
        logger.info("POST /api/group-members/request - 그룹 가입 신청 요청: 그룹 {}, 회원 {}", groupId, memberId);
        try {
            GroupMember membership = groupMemberService.requestGroupMembership(groupId, memberId);
            logger.info("POST /api/group-members/request - 신청 완료: {}", membership.getId());
            return ResponseEntity.ok(membership);
        } catch (RuntimeException e) {
            logger.warn("POST /api/group-members/request - 신청 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 그룹 가입 승인
    @PutMapping("/{id}/approve")
    public ResponseEntity<GroupMember> approveGroupMembership(@PathVariable Long id, 
                                                            @RequestParam String processorId) {
        logger.info("PUT /api/group-members/{}/approve - 그룹 가입 승인 요청, 처리자: {}", id, processorId);
        try {
            GroupMember membership = groupMemberService.processGroupMembership(id, GroupMemberStatus.APPROVED, processorId);
            logger.info("PUT /api/group-members/{}/approve - 승인 완료", id);
            return ResponseEntity.ok(membership);
        } catch (RuntimeException e) {
            logger.warn("PUT /api/group-members/{}/approve - 승인 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 그룹 가입 거절
    @PutMapping("/{id}/reject")
    public ResponseEntity<GroupMember> rejectGroupMembership(@PathVariable Long id, 
                                                           @RequestParam String processorId) {
        logger.info("PUT /api/group-members/{}/reject - 그룹 가입 거절 요청, 처리자: {}", id, processorId);
        try {
            GroupMember membership = groupMemberService.processGroupMembership(id, GroupMemberStatus.REJECTED, processorId);
            logger.info("PUT /api/group-members/{}/reject - 거절 완료", id);
            return ResponseEntity.ok(membership);
        } catch (RuntimeException e) {
            logger.warn("PUT /api/group-members/{}/reject - 거절 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 그룹의 멤버 목록 조회
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<GroupMember>> getMembersByGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/group-members/group/{} - 그룹별 멤버 목록 조회 요청 (페이지: {}, 크기: {})", groupId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupMember> members = groupMemberService.getMembersByGroup(groupId, pageable);
            logger.info("GET /api/group-members/group/{} - 조회 완료, 멤버 수: {}", groupId, members.getTotalElements());
            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            logger.warn("GET /api/group-members/group/{} - 조회 실패: {}", groupId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 회원의 그룹 가입 신청 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<GroupMember>> getMembershipsByMember(
            @PathVariable String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/group-members/member/{} - 회원별 가입 신청 목록 조회 요청 (페이지: {}, 크기: {})", memberId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupMember> memberships = groupMemberService.getMembershipsByMember(memberId, pageable);
            logger.info("GET /api/group-members/member/{} - 조회 완료, 신청 수: {}", memberId, memberships.getTotalElements());
            return ResponseEntity.ok(memberships);
        } catch (RuntimeException e) {
            logger.warn("GET /api/group-members/member/{} - 조회 실패: {}", memberId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 그룹의 승인된 멤버 수 조회
    @GetMapping("/group/{groupId}/approved-count")
    public ResponseEntity<Long> getApprovedMembersCount(@PathVariable Long groupId) {
        logger.info("GET /api/group-members/group/{}/approved-count - 승인된 멤버 수 조회 요청", groupId);
        try {
            long count = groupMemberService.getApprovedMembersCount(groupId);
            logger.info("GET /api/group-members/group/{}/approved-count - 조회 완료: {}", groupId, count);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            logger.warn("GET /api/group-members/group/{}/approved-count - 조회 실패: {}", groupId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 그룹의 대기중인 가입 신청 수 조회
    @GetMapping("/group/{groupId}/pending-count")
    public ResponseEntity<Long> getPendingMembersCount(@PathVariable Long groupId) {
        logger.info("GET /api/group-members/group/{}/pending-count - 대기중인 신청 수 조회 요청", groupId);
        try {
            long count = groupMemberService.getPendingMembersCount(groupId);
            logger.info("GET /api/group-members/group/{}/pending-count - 조회 완료: {}", groupId, count);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            logger.warn("GET /api/group-members/group/{}/pending-count - 조회 실패: {}", groupId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 