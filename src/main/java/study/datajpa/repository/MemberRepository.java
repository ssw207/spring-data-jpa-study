package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// spring data jpa가 구현클래스를 프록시로 만들어 주입
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {// entyty, id
    
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //길어지면 가독성이 떨어짐
    List<Member> findHelloBy(); // By가 끝에 붙으면 전체조회
    
    @Query("select m from Member m where m.username = :username and m.age = :age") //jpql 직접작성, 오타시 런타임에 오류감지
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t ")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names") // in절로 조회
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 컬랙션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m join m.team t",
            countQuery = "select count(m.username) from Member m") // 카운트 쿼리 분리가능
    Page<Member> findQueryByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // jpa executeUpdate 실행
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) // jpql에 페치조인을 추가하고 싶은경우
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @EntityGraph("Member.all")
    List<Member> findEntityGraph2ByUsername(@Param("username") String username);

    /**
     * 하이버네이트에서 제공하는 기능 readOnly를 설정하면 영속성 컨텍스트에서 캐싱하지 않음 즉 더티체킹이 불가능
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
