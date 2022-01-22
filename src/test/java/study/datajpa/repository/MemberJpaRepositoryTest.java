package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test; // jupiter -> junit5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        
        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);
        assertEquals(member1.getId(), findMember1.getId());
        assertEquals(member1.getUsername(), findMember1.getUsername());

        //다건 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();
        assertEquals(count, all.size());

        //삭제검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long count2 = memberJpaRepository.count();
        assertEquals(count2, 0);
    }
}