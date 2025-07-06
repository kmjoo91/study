package me.kmj.config;

import me.kmj.entity.Member;
import me.kmj.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final MemberRepository memberRepository;
    
    @Autowired
    public DataInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("데이터베이스 초기화 시작");
        
        // 기존 데이터가 없을 때만 샘플 데이터 삽입
        if (memberRepository.count() == 0) {
            insertSampleData();
            logger.info("샘플 데이터 삽입 완료");
        } else {
            logger.info("기존 데이터가 존재하여 샘플 데이터 삽입을 건너뜁니다");
        }
        
        logger.info("현재 저장된 멤버 수: {}", memberRepository.count());
    }
    
    private void insertSampleData() {
        Member member1 = new Member("test1", "김철수", "남성", "010-1234-5678", "서울시 강남구 테헤란로 123");
        Member member2 = new Member("test2", "이영희", "여성", "010-9876-5432", "부산시 해운대구 해운대로 456");
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        
        logger.info("샘플 데이터가 성공적으로 삽입되었습니다");
    }
} 