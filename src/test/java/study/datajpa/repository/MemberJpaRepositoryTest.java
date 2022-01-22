package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test; // jupiter -> junit5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class) // junit5 에서는 없어도됨
@SpringBootTest
@Transactional // 기본적으로 테스트인 경우 끝날때 롤백을 자동으로 시키고 flush도하지 않음
@Rollback(value = false) // 롤백하지 않도록 설정
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("이름");
        Member savedMember = memberJpaRepository.save(member);

        //when
        Member findMember = memberJpaRepository.find(savedMember.getId());
        
        //then
        assertEquals(findMember.getId(), savedMember.getId());
        assertEquals(findMember.getUsername(), savedMember.getUsername());
        assertEquals(findMember, savedMember);
    }
}