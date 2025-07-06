package me.kmj.controller;

import me.kmj.entity.Member;
import me.kmj.service.MemberService;
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
    
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        logger.info("GET /api/members - 모든 멤버 조회 요청");
        List<Member> members = memberService.getAllMembers();
        logger.info("GET /api/members - 조회 완료, 멤버 수: {}", members.size());
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable String id) {
        logger.info("GET /api/members/{} - 멤버 조회 요청", id);
        Optional<Member> member = memberService.getMemberById(id);
        if (member.isPresent()) {
            logger.info("GET /api/members/{} - 조회 성공", id);
            return ResponseEntity.ok(member.get());
        } else {
            logger.warn("GET /api/members/{} - 조회 실패 (존재하지 않음)", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        logger.info("POST /api/members - 새 멤버 생성 요청: {}", member.getId());
        Member savedMember = memberService.saveMember(member);
        logger.info("POST /api/members - 생성 완료: {}", savedMember.getId());
        return ResponseEntity.ok(savedMember);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable String id, @RequestBody Member member) {
        logger.info("PUT /api/members/{} - 멤버 수정 요청", id);
        member.setId(id);
        Member updatedMember = memberService.saveMember(member);
        logger.info("PUT /api/members/{} - 수정 완료", id);
        return ResponseEntity.ok(updatedMember);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        logger.info("DELETE /api/members/{} - 멤버 삭제 요청", id);
        memberService.deleteMember(id);
        logger.info("DELETE /api/members/{} - 삭제 완료", id);
        return ResponseEntity.ok().build();
    }
} 