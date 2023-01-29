package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
@Rollback(value = false) // true면 롤백됨
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

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

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // 페이지는 0부터, 3개를 가져오고 유저명 역순정렬
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when Page 타입으로 받으면 count 쿼리가 추가로 나간다.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent(); // 조회 데이터수
        long totalElements = page.getTotalElements(); // 전체 총건수

        assertEquals(content.size(), 3); // 페이징 사이즈가 3이므로 한번에 3개만 가져온다
        assertEquals(totalElements, 5); // 쿼리의 총 조회결과는 5개
        assertEquals(page.getNumber(), 0); // 페이지 번호. 0부터 시작한다
        assertEquals(page.getTotalPages(), 2); // 페이징수3, 총5이니까 3,2 이렇게 나눠짐
        assertEquals(page.isFirst(), true); // 조회결과가 첫페이지라는뜻
        assertEquals(page.hasNext(), true); // 다음페이지가 있다는뜻
    }

    @Test
    public void paging2() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest); // 전체 페이지 수를 가져오지 않음. slice limit는 4임

        //then
        List<Member> content = page.getContent();

        assertEquals(content.size(), 3);
        assertEquals(page.getNumber(), 0);
        assertEquals(page.isFirst(), true);
        assertEquals(page.hasNext(), true); // 페이지수 + 1개의 데이터를 조회해 오기떄문에 조회결과가 4건이면 다음데이터가 있다고 판단한다
    }

    @Test
    public void paging3() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        List<Member> page = memberRepository.findListByAge(age, pageRequest); // 0~limit까지만 끊어서 가져옴

        //then
        assertEquals(page.size(), 3);

    }

    /**
     * Page의 count 쿼리는 기본적으로 본 쿼리와 같은 쿼리를 이용한다.
     * 문제는 본 쿼리가 left join이 많아 복잡하고
     * count 쿼리에는 드라이빙 테이블만 집계하면 되는경우
     * 본 쿼리를 똑같이 사용하면 성능이 느리다.
     * 이런경우 최적화 하는법
     */
    @Test
    public void paging4_카운트쿼리를_분리해_최적화한다() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findQueryByAge(age, pageRequest); // 0~limit까지만 끊어서 가져옴
        
        // DTO변환이 필요한경우
        Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int age = 20;

        //벌크연산은 영속성 컨텍스트를 무시하고 변경
        int resultCnt = memberRepository.bulkAgePlus(age);
        em.flush(); // 변경점이 DB에 반영
        em.clear(); // 영속성 컨텍스트 초기화

        // 영속성 컨텍스트가 초기화 되어 벌크연산으로 변경된 정보가 적용됨
        Member findMember5 = memberRepository.findMemberByUsername("member5");

        //then
        assertEquals(3, resultCnt);
        assertEquals(findMember5.getAge(), 41);
    }
    
    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @DisplayName("JPQL 패치 조인으로 Member와 Team 한번에 조회한다")
    @Test
    public void findMemberLazy2() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when : lazy로딩이라도 패치조인을 사용하면 한번에 조회가 가능하다. 단 주 엔티티가 N:1 관계일
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) { // 이미 members 조회 시점에 team도 영속화 되어 있기 때문에 team에 접근할때 쿼리가 안나간다
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }

    @DisplayName("entity graph로 Member와 Team 한번에 조회한다")
    @Test
    public void findMemberLazy3() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }
    
    @DisplayName("NamedEntityGraph로 사전에 정의한 EntityGraph를 실행할 수 있다")
    @Test
    public void findMemberLazy4() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraph2ByUsername("member1");

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }

    @Test
    public void queryHint() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        memberRepository.findLockByUsername("member1");
    }
    
    @Test
    public void callCustom() throws Exception {
        //given
        List<Member> result = memberRepository.findMemberCustom();
    }
}