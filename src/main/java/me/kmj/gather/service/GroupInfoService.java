package me.kmj.gather.service;

import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.entity.Member;
import me.kmj.gather.repository.GroupInfoRepository;
import me.kmj.gather.repository.GroupMemberRepository;
import me.kmj.gather.dto.GroupInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class GroupInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupInfoService.class);
    
    private final GroupInfoRepository groupInfoRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberService memberService;
    
    @Autowired
    public GroupInfoService(GroupInfoRepository groupInfoRepository,
                          GroupMemberRepository groupMemberRepository,
                          MemberService memberService) {
        this.groupInfoRepository = groupInfoRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.memberService = memberService;
    }
    
    // 그룹 생성
    public GroupInfo createGroup(GroupInfoDto groupInfoDto, String createdById) {
        logger.info("그룹 생성 요청 - 그룹명: {}, 생성자: {}", groupInfoDto.getName(), createdById);
        
        Optional<Member> creator = memberService.getMemberById(createdById);
        if (creator.isEmpty()) {
            logger.warn("그룹 생성 실패 - 존재하지 않는 생성자: {}", createdById);
            throw new RuntimeException("존재하지 않는 회원입니다: " + createdById);
        }
        
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setName(groupInfoDto.getName());
        groupInfo.setDescription(groupInfoDto.getDescription());
        groupInfo.setCreatedBy(creator.get());
        
        GroupInfo savedGroup = groupInfoRepository.save(groupInfo);
        logger.info("그룹 생성 완료 - ID: {}, 그룹명: {}", savedGroup.getId(), savedGroup.getName());
        return savedGroup;
    }
    
    // 그룹 수정
    public GroupInfo updateGroup(Long groupId, GroupInfoDto groupInfoDto, String createdById) {
        logger.info("그룹 수정 요청 - ID: {}, 수정자: {}", groupId, createdById);
        
        Optional<GroupInfo> existingGroup = groupInfoRepository.findById(groupId);
        if (existingGroup.isEmpty()) {
            logger.warn("그룹 수정 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        GroupInfo groupInfo = existingGroup.get();
        if (!groupInfo.getCreatedBy().getId().equals(createdById)) {
            logger.warn("그룹 수정 실패 - 권한 없음: 그룹 {}, 수정자 {}", groupId, createdById);
            throw new RuntimeException("그룹을 수정할 권한이 없습니다");
        }
        
        groupInfo.setName(groupInfoDto.getName());
        groupInfo.setDescription(groupInfoDto.getDescription());
        
        GroupInfo updatedGroup = groupInfoRepository.save(groupInfo);
        logger.info("그룹 수정 완료 - ID: {}", updatedGroup.getId());
        return updatedGroup;
    }
    
    // 그룹 삭제
    public void deleteGroup(Long groupId, String createdById) {
        logger.info("그룹 삭제 요청 - ID: {}, 삭제자: {}", groupId, createdById);
        
        Optional<GroupInfo> existingGroup = groupInfoRepository.findById(groupId);
        if (existingGroup.isEmpty()) {
            logger.warn("그룹 삭제 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        GroupInfo groupInfo = existingGroup.get();
        if (!groupInfo.getCreatedBy().getId().equals(createdById)) {
            logger.warn("그룹 삭제 실패 - 권한 없음: 그룹 {}, 삭제자 {}", groupId, createdById);
            throw new RuntimeException("그룹을 삭제할 권한이 없습니다");
        }
        
        groupInfoRepository.deleteById(groupId);
        logger.info("그룹 삭제 완료 - ID: {}", groupId);
    }
    
    // 그룹 상세 조회
    public Optional<GroupInfo> getGroupById(Long groupId) {
        logger.debug("그룹 상세 조회 요청 - ID: {}", groupId);
        Optional<GroupInfo> groupInfo = groupInfoRepository.findById(groupId);
        if (groupInfo.isPresent()) {
            logger.debug("그룹 상세 조회 성공 - ID: {}, 그룹명: {}", groupId, groupInfo.get().getName());
        } else {
            logger.debug("그룹 상세 조회 실패 - ID: {} (존재하지 않음)", groupId);
        }
        return groupInfo;
    }
    
    // 페이징 처리된 그룹 목록 조회
    public Page<GroupInfo> getAllGroups(Pageable pageable) {
        logger.debug("그룹 목록 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<GroupInfo> groups = groupInfoRepository.findAllByOrderByCreatedAtDesc(pageable);
        logger.debug("조회된 그룹 수: {}", groups.getTotalElements());
        return groups;
    }
    
    // 그룹명으로 그룹 검색
    public Page<GroupInfo> searchGroupsByName(String keyword, Pageable pageable) {
        logger.debug("그룹명 검색 요청 - 키워드: {}", keyword);
        Page<GroupInfo> groups = groupInfoRepository.findByNameContaining(keyword, pageable);
        logger.debug("검색된 그룹 수: {}", groups.getTotalElements());
        return groups;
    }
    
    // 특정 사용자가 생성한 그룹 목록 조회
    public Page<GroupInfo> getGroupsByCreator(String createdById, Pageable pageable) {
        logger.debug("사용자별 그룹 목록 조회 요청 - 생성자: {}", createdById);
        
        Optional<Member> creator = memberService.getMemberById(createdById);
        if (creator.isEmpty()) {
            logger.warn("사용자별 그룹 목록 조회 실패 - 존재하지 않는 사용자: {}", createdById);
            throw new RuntimeException("존재하지 않는 회원입니다: " + createdById);
        }
        
        Page<GroupInfo> groups = groupInfoRepository.findByCreatedByOrderByCreatedAtDesc(creator.get(), pageable);
        logger.debug("조회된 그룹 수: {}", groups.getTotalElements());
        return groups;
    }
} 