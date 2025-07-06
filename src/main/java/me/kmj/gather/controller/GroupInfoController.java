package me.kmj.gather.controller;

import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.service.GroupInfoService;
import me.kmj.gather.dto.GroupInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupInfoController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupInfoController.class);
    
    private final GroupInfoService groupInfoService;
    
    @Autowired
    public GroupInfoController(GroupInfoService groupInfoService) {
        this.groupInfoService = groupInfoService;
    }
    
    // 그룹 생성
    @PostMapping
    public ResponseEntity<GroupInfo> createGroup(@RequestBody GroupInfoDto groupInfoDto, 
                                               @RequestParam String createdById) {
        logger.info("POST /api/groups - 그룹 생성 요청: {}, 생성자: {}", groupInfoDto.getName(), createdById);
        try {
            GroupInfo savedGroup = groupInfoService.createGroup(groupInfoDto, createdById);
            logger.info("POST /api/groups - 생성 완료: {}", savedGroup.getId());
            return ResponseEntity.ok(savedGroup);
        } catch (RuntimeException e) {
            logger.warn("POST /api/groups - 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 그룹 수정
    @PutMapping("/{id}")
    public ResponseEntity<GroupInfo> updateGroup(@PathVariable Long id, 
                                               @RequestBody GroupInfoDto groupInfoDto,
                                               @RequestParam String createdById) {
        logger.info("PUT /api/groups/{} - 그룹 수정 요청, 수정자: {}", id, createdById);
        try {
            GroupInfo updatedGroup = groupInfoService.updateGroup(id, groupInfoDto, createdById);
            logger.info("PUT /api/groups/{} - 수정 완료", id);
            return ResponseEntity.ok(updatedGroup);
        } catch (RuntimeException e) {
            logger.warn("PUT /api/groups/{} - 수정 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 그룹 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, 
                                          @RequestParam String createdById) {
        logger.info("DELETE /api/groups/{} - 그룹 삭제 요청, 삭제자: {}", id, createdById);
        try {
            groupInfoService.deleteGroup(id, createdById);
            logger.info("DELETE /api/groups/{} - 삭제 완료", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/groups/{} - 삭제 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 그룹 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<GroupInfo> getGroupById(@PathVariable Long id) {
        logger.info("GET /api/groups/{} - 그룹 상세 조회 요청", id);
        Optional<GroupInfo> group = groupInfoService.getGroupById(id);
        if (group.isPresent()) {
            logger.info("GET /api/groups/{} - 조회 성공", id);
            return ResponseEntity.ok(group.get());
        } else {
            logger.warn("GET /api/groups/{} - 조회 실패 (존재하지 않음)", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    // 페이징 처리된 그룹 목록 조회
    @GetMapping
    public ResponseEntity<Page<GroupInfo>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/groups - 그룹 목록 조회 요청 (페이지: {}, 크기: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupInfo> groups = groupInfoService.getAllGroups(pageable);
        logger.info("GET /api/groups - 조회 완료, 총 그룹 수: {}", groups.getTotalElements());
        return ResponseEntity.ok(groups);
    }
    
    // 그룹명으로 그룹 검색
    @GetMapping("/search")
    public ResponseEntity<Page<GroupInfo>> searchGroupsByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/groups/search - 그룹 검색 요청 (키워드: {}, 페이지: {}, 크기: {})", keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupInfo> groups = groupInfoService.searchGroupsByName(keyword, pageable);
        logger.info("GET /api/groups/search - 검색 완료, 검색된 그룹 수: {}", groups.getTotalElements());
        return ResponseEntity.ok(groups);
    }
    
    // 특정 사용자가 생성한 그룹 목록 조회
    @GetMapping("/creator/{createdById}")
    public ResponseEntity<Page<GroupInfo>> getGroupsByCreator(
            @PathVariable String createdById,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /api/groups/creator/{} - 사용자별 그룹 목록 조회 요청 (페이지: {}, 크기: {})", createdById, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupInfo> groups = groupInfoService.getGroupsByCreator(createdById, pageable);
            logger.info("GET /api/groups/creator/{} - 조회 완료, 그룹 수: {}", createdById, groups.getTotalElements());
            return ResponseEntity.ok(groups);
        } catch (RuntimeException e) {
            logger.warn("GET /api/groups/creator/{} - 조회 실패: {}", createdById, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 