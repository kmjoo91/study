package me.kmj.gather.controller;

import me.kmj.gather.entity.Member;
import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.service.MemberService;
import me.kmj.gather.service.GroupInfoService;
import me.kmj.gather.service.GroupMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final MemberService memberService;
    private final GroupInfoService groupInfoService;
    private final GroupMemberService groupMemberService;
    
    @Autowired
    public AdminController(MemberService memberService,
                         GroupInfoService groupInfoService,
                         GroupMemberService groupMemberService) {
        this.memberService = memberService;
        this.groupInfoService = groupInfoService;
        this.groupMemberService = groupMemberService;
    }
    
    // 전체 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("GET /api/admin/statistics - 전체 통계 조회 요청");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 전체 회원 수
        List<Member> allMembers = memberService.getAllMembers();
        statistics.put("totalMembers", allMembers.size());
        
        // 전체 그룹 수
        Page<GroupInfo> allGroups = groupInfoService.getAllGroups(PageRequest.of(0, Integer.MAX_VALUE));
        statistics.put("totalGroups", allGroups.getTotalElements());
        
        // 전체 그룹 가입 신청 수 (대기중)
        long totalPendingRequests = 0;
        for (GroupInfo group : allGroups.getContent()) {
            totalPendingRequests += groupMemberService.getPendingMembersCount(group.getId());
        }
        statistics.put("totalPendingGroupRequests", totalPendingRequests);
        
        // 전체 승인된 그룹 멤버 수
        long totalApprovedMembers = 0;
        for (GroupInfo group : allGroups.getContent()) {
            totalApprovedMembers += groupMemberService.getApprovedMembersCount(group.getId());
        }
        statistics.put("totalApprovedGroupMembers", totalApprovedMembers);
        
        logger.info("GET /api/admin/statistics - 조회 완료");
        return ResponseEntity.ok(statistics);
    }
    
    // 그룹별 상세 통계 조회
    @GetMapping("/groups/{groupId}/statistics")
    public ResponseEntity<Map<String, Object>> getGroupStatistics(@PathVariable Long groupId) {
        logger.info("GET /api/admin/groups/{}/statistics - 그룹별 통계 조회 요청", groupId);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 승인된 멤버 수
        long approvedCount = groupMemberService.getApprovedMembersCount(groupId);
        statistics.put("approvedMembers", approvedCount);
        
        // 대기중인 가입 신청 수
        long pendingCount = groupMemberService.getPendingMembersCount(groupId);
        statistics.put("pendingRequests", pendingCount);
        
        // 총 가입 신청 수
        statistics.put("totalRequests", approvedCount + pendingCount);
        
        logger.info("GET /api/admin/groups/{}/statistics - 조회 완료", groupId);
        return ResponseEntity.ok(statistics);
    }
    
    // 전체 그룹 목록 조회 (관리자용)
    @GetMapping("/groups")
    public ResponseEntity<Page<GroupInfo>> getAllGroupsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/admin/groups - 전체 그룹 목록 조회 요청 (페이지: {}, 크기: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupInfo> groups = groupInfoService.getAllGroups(pageable);
        logger.info("GET /api/admin/groups - 조회 완료, 총 그룹 수: {}", groups.getTotalElements());
        return ResponseEntity.ok(groups);
    }
    
    // 전체 회원 목록 조회 (관리자용)
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembersForAdmin() {
        logger.info("GET /api/admin/members - 전체 회원 목록 조회 요청");
        List<Member> members = memberService.getAllMembers();
        logger.info("GET /api/admin/members - 조회 완료, 총 회원 수: {}", members.size());
        return ResponseEntity.ok(members);
    }
} 