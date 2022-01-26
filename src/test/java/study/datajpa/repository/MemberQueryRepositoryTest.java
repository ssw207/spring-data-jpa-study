package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false) // true면 롤백됨
class MemberQueryRepositoryTest {
    @Autowired MemberQueryRepository memberQueryRepository;
    @Autowired MemberRepository memberRepository;
    
    @Test
    public void test() throws Exception {
        //given
        memberRepository.save(new Member("member"));
        //when
        List<Member> result = memberQueryRepository.findAllMember();
        //then
        assertEquals(result.size(), 1);
    }
}