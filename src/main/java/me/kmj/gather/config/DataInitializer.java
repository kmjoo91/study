package me.kmj.gather.config;

import me.kmj.gather.entity.Member;
import me.kmj.gather.entity.GroupInfo;
import me.kmj.gather.entity.GroupMember;
import me.kmj.gather.repository.MemberRepository;
import me.kmj.gather.repository.GroupInfoRepository;
import me.kmj.gather.repository.GroupMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final MemberRepository memberRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final GroupMemberRepository groupMemberRepository;
    
    @Autowired
    public DataInitializer(MemberRepository memberRepository,
                          GroupInfoRepository groupInfoRepository,
                          GroupMemberRepository groupMemberRepository) {
        this.memberRepository = memberRepository;
        this.groupInfoRepository = groupInfoRepository;
        this.groupMemberRepository = groupMemberRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("데이터 초기화 시작");
        
        // 회원 데이터 초기화
        initializeMembers();
        
        // 그룹 데이터 초기화
        initializeGroups();
        
        // 그룹 멤버 데이터 초기화
        initializeGroupMembers();
        
        logger.info("데이터 초기화 완료");
    }
    
    private void initializeMembers() {
        if (memberRepository.count() == 0) {
            logger.info("회원 데이터 초기화");
            
            List<Member> members = Arrays.asList(
                new Member("user001", "김철수", "남성", "010-1234-5678", "서울시 강남구"),
                new Member("user002", "이영희", "여성", "010-2345-6789", "서울시 서초구"),
                new Member("user003", "박민수", "남성", "010-3456-7890", "서울시 마포구"),
                new Member("user004", "정수진", "여성", "010-4567-8901", "서울시 송파구"),
                new Member("user005", "최동현", "남성", "010-5678-9012", "서울시 영등포구")
            );
            
            memberRepository.saveAll(members);
            logger.info("회원 데이터 초기화 완료 - {}명", members.size());
        }
    }
    
    private void initializeGroups() {
        if (groupInfoRepository.count() == 0) {
            logger.info("그룹 데이터 초기화");
            
            List<Member> members = memberRepository.findAll();
            if (members.isEmpty()) {
                logger.warn("회원 데이터가 없어 그룹 데이터 초기화를 건너뜁니다");
                return;
            }
            
            List<GroupInfo> groups = Arrays.asList(
                new GroupInfo(null, "개발자 커뮤니티", "개발자들이 정보를 공유하고 네트워킹하는 그룹", 
                             members.get(0), LocalDateTime.now()),
                new GroupInfo(null, "독서 모임", "다양한 책을 읽고 토론하는 그룹", 
                             members.get(1), LocalDateTime.now()),
                new GroupInfo(null, "운동 모임", "함께 운동하고 건강을 관리하는 그룹", 
                             members.get(2), LocalDateTime.now())
            );
            
            groupInfoRepository.saveAll(groups);
            logger.info("그룹 데이터 초기화 완료 - {}개", groups.size());
        }
    }
    
    private void initializeGroupMembers() {
        if (groupMemberRepository.count() == 0) {
            logger.info("그룹 멤버 데이터 초기화");
            
            List<Member> members = memberRepository.findAll();
            List<GroupInfo> groups = groupInfoRepository.findAll();
            
            if (members.isEmpty() || groups.isEmpty()) {
                logger.warn("회원 또는 그룹 데이터가 없어 그룹 멤버 데이터 초기화를 건너뜁니다");
                return;
            }
            
            List<GroupMember> groupMembers = Arrays.asList(
                new GroupMember(null, members.get(1), groups.get(0), GroupMember.GroupMemberStatus.PENDING, 
                               LocalDateTime.now(), null),
                new GroupMember(null, members.get(2), groups.get(0), GroupMember.GroupMemberStatus.APPROVED, 
                               LocalDateTime.now(), LocalDateTime.now()),
                new GroupMember(null, members.get(0), groups.get(1), GroupMember.GroupMemberStatus.PENDING, 
                               LocalDateTime.now(), null),
                new GroupMember(null, members.get(3), groups.get(1), GroupMember.GroupMemberStatus.APPROVED, 
                               LocalDateTime.now(), LocalDateTime.now()),
                new GroupMember(null, members.get(1), groups.get(2), GroupMember.GroupMemberStatus.REJECTED, 
                               LocalDateTime.now(), LocalDateTime.now())
            );
            
            groupMemberRepository.saveAll(groupMembers);
            logger.info("그룹 멤버 데이터 초기화 완료 - {}개", groupMembers.size());
        }
    }
} 