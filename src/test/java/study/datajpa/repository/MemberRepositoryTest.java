package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
@Rollback(value = false) // true면 롤백됨
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    
    @Test
    public void testMember() throws Exception {
        //spring data jpa 구현체를 만들어 주입해줌
        System.out.println(memberRepository.getClass());

        //given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);
        assertEquals(member1.getId(), findMember1.getId());
        assertEquals(member1.getUsername(), findMember1.getUsername());

        //다건 조회 검증
        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();
        assertEquals(count, all.size());

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long count2 = memberRepository.count();
        assertEquals(count2, 0);
    }

    @Test
    public void findByUsernameAndAgeGreateThen() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        //then
        assertEquals(result.get(0).getUsername(), "AAA");
        assertEquals(result.get(0).getAge(), 20);
        assertEquals(result.size(), 1);
    }
    
    @Test
    public void findHelloBy() throws Exception {
        List<Member> helloBy = memberRepository.findHelloBy();
    }

    @Test
    public void testQuery() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findUser("AAA", 20);

        //then
        assertEquals(result.get(0).getUsername(), "AAA");
        assertEquals(result.get(0).getAge(), 20);
        assertEquals(result.size(), 1);
    }

    @Test
    public void findUsernameList() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<String> result = memberRepository.findUsernameList();

        //then
        assertEquals(result.size(), 2);
    }
    
    @Test
    public void findMemberDto() throws Exception {
        Team team  = new Team("teamA");
        teamRepository.save(team);

        //given
        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);

        member1.changeTeam(team);

        //when
        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto.toString());
        }
        //then
        //assertEquals(result.size(), 2);
    }

    @Test
    public void findByNames() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = "+ member);
        }
    }

    @Test
    public void returnType() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        assertEquals(aaa.size(), 1);

        //"컬랙션은 조회결과가 없어도 null이 아니다"
        List<Member> empty = memberRepository.findListByUsername("임의값"); 
        assertEquals(empty.isEmpty(), true);

        Member findMember = memberRepository.findMemberByUsername("AAA");
        assertEquals(findMember.getId(), member1.getId());
        
        Member emptyMember = memberRepository.findMemberByUsername("임의값");
        assertNull(emptyMember);

        Optional<Member> findMember2 = memberRepository.findOptionalByUsername("AAA"); //
        assertEquals(findMember2.get().getId(), member1.getId());

        Optional<Member> emptyMember2 = memberRepository.findOptionalByUsername("임의값");
        assertEquals(emptyMember2.isPresent(), false);
    }
}


