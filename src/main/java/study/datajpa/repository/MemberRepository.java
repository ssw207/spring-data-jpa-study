package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// spring data jpa가 구현클래스를 프록시로 만들어 주입
public interface MemberRepository extends JpaRepository<Member, Long> {// entyty, id
    
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
}
