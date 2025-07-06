package me.kmj.gather.controller;

import me.kmj.gather.entity.Member;
import me.kmj.gather.service.MemberService;
import me.kmj.gather.dto.MemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@RequestBody MemberDto memberDto) {
        logger.info("POST /api/members/register - 회원가입 요청: {}", memberDto.getId());
        try {
            Member savedMember = memberService.registerMember(memberDto);
            logger.info("POST /api/members/register - 회원가입 완료: {}", savedMember.getId());
            return ResponseEntity.ok(savedMember);
        } catch (RuntimeException e) {
            logger.warn("POST /api/members/register - 회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 전체 회원 목록 조회
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        logger.info("GET /api/members - 전체 회원 목록 조회 요청");
        List<Member> members = memberService.getAllMembers();
        logger.info("GET /api/members - 조회 완료, 회원 수: {}", members.size());
        return ResponseEntity.ok(members);
    }
    
    // 회원 조회 (ID로)
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable String id) {
        logger.info("GET /api/members/{} - 회원 조회 요청", id);
        Optional<Member> member = memberService.getMemberById(id);
        if (member.isPresent()) {
            logger.info("GET /api/members/{} - 조회 성공", id);
            return ResponseEntity.ok(member.get());
        } else {
            logger.warn("GET /api/members/{} - 조회 실패 (존재하지 않음)", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable String id, @RequestBody MemberDto memberDto) {
        logger.info("PUT /api/members/{} - 회원 정보 수정 요청", id);
        try {
            Member updatedMember = memberService.updateMember(id, memberDto);
            logger.info("PUT /api/members/{} - 수정 완료", id);
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            logger.warn("PUT /api/members/{} - 수정 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        logger.info("DELETE /api/members/{} - 회원 삭제 요청", id);
        try {
            memberService.deleteMember(id);
            logger.info("DELETE /api/members/{} - 삭제 완료", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/members/{} - 삭제 실패: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 