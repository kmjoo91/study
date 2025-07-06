package me.kmj.gather.service;

import me.kmj.gather.entity.Member;
import me.kmj.gather.repository.MemberRepository;
import me.kmj.gather.dto.MemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    // 회원가입
    public Member registerMember(MemberDto memberDto) {
        logger.info("회원가입 요청: {}", memberDto.getId());
        
        // ID 중복 확인
        if (memberRepository.existsById(memberDto.getId())) {
            logger.warn("회원가입 실패 - ID 중복: {}", memberDto.getId());
            throw new RuntimeException("이미 존재하는 ID입니다: " + memberDto.getId());
        }
        
        Member member = new Member(
            memberDto.getId(),
            memberDto.getName(),
            memberDto.getGender(),
            memberDto.getPhoneNumber(),
            memberDto.getAddress()
        );
        
        Member savedMember = memberRepository.save(member);
        logger.info("회원가입 완료: {}", savedMember.getId());
        return savedMember;
    }
    
    // 회원 조회 (ID로)
    public Optional<Member> getMemberById(String id) {
        logger.debug("회원 조회 요청 - ID: {}", id);
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            logger.debug("회원 조회 성공 - ID: {}, 이름: {}", id, member.get().getName());
        } else {
            logger.debug("회원 조회 실패 - ID: {} (존재하지 않음)", id);
        }
        return member;
    }
    
    // 전체 회원 목록 조회
    public List<Member> getAllMembers() {
        logger.debug("전체 회원 목록 조회 요청");
        List<Member> members = memberRepository.findAll();
        logger.debug("조회된 회원 수: {}", members.size());
        return members;
    }
    
    // 회원 정보 수정
    public Member updateMember(String id, MemberDto memberDto) {
        logger.info("회원 정보 수정 요청 - ID: {}", id);
        
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isEmpty()) {
            logger.warn("회원 정보 수정 실패 - 존재하지 않는 ID: {}", id);
            throw new RuntimeException("존재하지 않는 회원입니다: " + id);
        }
        
        Member member = existingMember.get();
        member.setName(memberDto.getName());
        member.setGender(memberDto.getGender());
        member.setPhoneNumber(memberDto.getPhoneNumber());
        member.setAddress(memberDto.getAddress());
        
        Member updatedMember = memberRepository.save(member);
        logger.info("회원 정보 수정 완료 - ID: {}", updatedMember.getId());
        return updatedMember;
    }
    
    // 회원 삭제
    public void deleteMember(String id) {
        logger.info("회원 삭제 요청 - ID: {}", id);
        
        if (!memberRepository.existsById(id)) {
            logger.warn("회원 삭제 실패 - 존재하지 않는 ID: {}", id);
            throw new RuntimeException("존재하지 않는 회원입니다: " + id);
        }
        
        memberRepository.deleteById(id);
        logger.info("회원 삭제 완료 - ID: {}", id);
    }
} 