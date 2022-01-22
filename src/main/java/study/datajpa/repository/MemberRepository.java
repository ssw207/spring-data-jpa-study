package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

// spring data jpa가 구현클래스를 프록시로 만들어 주입
public interface MemberRepository extends JpaRepository<Member, Long> {// entyty, id

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findHelloBy(); // By가 끝에 붙으면 전체조회
}
