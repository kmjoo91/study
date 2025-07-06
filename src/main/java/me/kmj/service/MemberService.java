package me.kmj.service;

import me.kmj.entity.Member;
import me.kmj.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    public List<Member> getAllMembers() {
        logger.debug("모든 멤버 조회 요청");
        List<Member> members = memberRepository.findAll();
        logger.debug("조회된 멤버 수: {}", members.size());
        return members;
    }
    
    public Optional<Member> getMemberById(String id) {
        logger.debug("멤버 조회 요청 - ID: {}", id);
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            logger.debug("멤버 조회 성공 - ID: {}, 이름: {}", id, member.get().getName());
        } else {
            logger.debug("멤버 조회 실패 - ID: {} (존재하지 않음)", id);
        }
        return member;
    }
    
    public Member saveMember(Member member) {
        logger.debug("멤버 저장 요청 - ID: {}, 이름: {}", member.getId(), member.getName());
        Member savedMember = memberRepository.save(member);
        logger.info("멤버 저장 완료 - ID: {}, 이름: {}", savedMember.getId(), savedMember.getName());
        return savedMember;
    }
    
    public void deleteMember(String id) {
        logger.debug("멤버 삭제 요청 - ID: {}", id);
        memberRepository.deleteById(id);
        logger.info("멤버 삭제 완료 - ID: {}", id);
    }
} 