package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 인터페이스명 + Impl 네이밍을 한 경우
 * spring data jpa 에서 자동으로 구현체를 찾아 호출해준다.
 * ex) MemberRepositoryImpl
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
