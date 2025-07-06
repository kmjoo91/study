package me.kmj.gather.service;

import me.kmj.gather.entity.GroupMember;
import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.entity.Member;
import me.kmj.gather.entity.GroupMember.GroupMemberStatus;
import me.kmj.gather.repository.GroupMemberRepository;
import me.kmj.gather.dto.GroupMemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupMemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupMemberService.class);
    
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInfoService groupInfoService;
    private final MemberService memberService;
    
    @Autowired
    public GroupMemberService(GroupMemberRepository groupMemberRepository,
                            GroupInfoService groupInfoService,
                            MemberService memberService) {
        this.groupMemberRepository = groupMemberRepository;
        this.groupInfoService = groupInfoService;
        this.memberService = memberService;
    }
    
    // 그룹 가입 신청
    public GroupMember requestGroupMembership(Long groupId, String memberId) {
        logger.info("그룹 가입 신청 요청 - 그룹: {}, 회원: {}", groupId, memberId);
        
        // 그룹 존재 확인
        Optional<GroupInfo> group = groupInfoService.getGroupById(groupId);
        if (group.isEmpty()) {
            logger.warn("그룹 가입 신청 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        // 회원 존재 확인
        Optional<Member> member = memberService.getMemberById(memberId);
        if (member.isEmpty()) {
            logger.warn("그룹 가입 신청 실패 - 존재하지 않는 회원: {}", memberId);
            throw new RuntimeException("존재하지 않는 회원입니다: " + memberId);
        }
        
        // 이미 신청했는지 확인
        Optional<GroupMember> existingMembership = groupMemberRepository.findByGroupAndMember(group.get(), member.get());
        if (existingMembership.isPresent()) {
            logger.warn("그룹 가입 신청 실패 - 이미 신청됨: 그룹 {}, 회원 {}", groupId, memberId);
            throw new RuntimeException("이미 가입 신청한 그룹입니다");
        }
        
        // 본인이 생성한 그룹인지 확인
        if (group.get().getCreatedBy().getId().equals(memberId)) {
            logger.warn("그룹 가입 신청 실패 - 본인이 생성한 그룹: 그룹 {}, 회원 {}", groupId, memberId);
            throw new RuntimeException("본인이 생성한 그룹에는 가입 신청할 수 없습니다");
        }
        
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group.get());
        groupMember.setMember(member.get());
        groupMember.setStatus(GroupMemberStatus.PENDING);
        
        GroupMember savedMembership = groupMemberRepository.save(groupMember);
        logger.info("그룹 가입 신청 완료 - ID: {}, 그룹: {}, 회원: {}", 
                   savedMembership.getId(), groupId, memberId);
        return savedMembership;
    }
    
    // 그룹 가입 승인/거절
    public GroupMember processGroupMembership(Long membershipId, GroupMemberStatus status, String processorId) {
        logger.info("그룹 가입 처리 요청 - 멤버십ID: {}, 상태: {}, 처리자: {}", membershipId, status, processorId);
        
        Optional<GroupMember> membership = groupMemberRepository.findById(membershipId);
        if (membership.isEmpty()) {
            logger.warn("그룹 가입 처리 실패 - 존재하지 않는 멤버십: {}", membershipId);
            throw new RuntimeException("존재하지 않는 가입 신청입니다: " + membershipId);
        }
        
        GroupMember membershipEntity = membership.get();
        
        // 처리 권한 확인 (그룹 생성자만 처리 가능)
        if (!membershipEntity.getGroup().getCreatedBy().getId().equals(processorId)) {
            logger.warn("그룹 가입 처리 실패 - 권한 없음: 멤버십ID {}, 처리자 {}", membershipId, processorId);
            throw new RuntimeException("그룹 가입 신청을 처리할 권한이 없습니다");
        }
        
        // 이미 처리된 신청인지 확인
        if (membershipEntity.getStatus() != GroupMemberStatus.PENDING) {
            logger.warn("그룹 가입 처리 실패 - 이미 처리됨: 멤버십ID {}, 현재상태 {}", membershipId, membershipEntity.getStatus());
            throw new RuntimeException("이미 처리된 가입 신청입니다");
        }
        
        membershipEntity.setStatus(status);
        membershipEntity.setProcessedAt(LocalDateTime.now());
        
        GroupMember processedMembership = groupMemberRepository.save(membershipEntity);
        logger.info("그룹 가입 처리 완료 - ID: {}, 상태: {}", processedMembership.getId(), status);
        return processedMembership;
    }
    
    // 특정 그룹의 멤버 목록 조회
    public Page<GroupMember> getMembersByGroup(Long groupId, Pageable pageable) {
        logger.debug("그룹별 멤버 목록 조회 요청 - 그룹: {}", groupId);
        
        Optional<GroupInfo> group = groupInfoService.getGroupById(groupId);
        if (group.isEmpty()) {
            logger.warn("그룹별 멤버 목록 조회 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        Page<GroupMember> members = groupMemberRepository.findByGroupOrderByRequestedAtDesc(group.get(), pageable);
        logger.debug("조회된 멤버 수: {}", members.getTotalElements());
        return members;
    }
    
    // 특정 회원의 그룹 가입 신청 목록 조회
    public Page<GroupMember> getMembershipsByMember(String memberId, Pageable pageable) {
        logger.debug("회원별 그룹 가입 신청 목록 조회 요청 - 회원: {}", memberId);
        
        Optional<Member> member = memberService.getMemberById(memberId);
        if (member.isEmpty()) {
            logger.warn("회원별 그룹 가입 신청 목록 조회 실패 - 존재하지 않는 회원: {}", memberId);
            throw new RuntimeException("존재하지 않는 회원입니다: " + memberId);
        }
        
        Page<GroupMember> memberships = groupMemberRepository.findByMemberOrderByRequestedAtDesc(member.get(), pageable);
        logger.debug("조회된 가입 신청 수: {}", memberships.getTotalElements());
        return memberships;
    }
    
    // 특정 그룹의 승인된 멤버 수 조회
    public long getApprovedMembersCount(Long groupId) {
        logger.debug("승인된 멤버 수 조회 요청 - 그룹: {}", groupId);
        
        Optional<GroupInfo> group = groupInfoService.getGroupById(groupId);
        if (group.isEmpty()) {
            logger.warn("승인된 멤버 수 조회 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        long count = groupMemberRepository.countApprovedMembers(group.get());
        logger.debug("승인된 멤버 수: {}", count);
        return count;
    }
    
    // 특정 그룹의 대기중인 가입 신청 수 조회
    public long getPendingMembersCount(Long groupId) {
        logger.debug("대기중인 가입 신청 수 조회 요청 - 그룹: {}", groupId);
        
        Optional<GroupInfo> group = groupInfoService.getGroupById(groupId);
        if (group.isEmpty()) {
            logger.warn("대기중인 가입 신청 수 조회 실패 - 존재하지 않는 그룹: {}", groupId);
            throw new RuntimeException("존재하지 않는 그룹입니다: " + groupId);
        }
        
        long count = groupMemberRepository.countPendingMembers(group.get());
        logger.debug("대기중인 가입 신청 수: {}", count);
        return count;
    }
} 