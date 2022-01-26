package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberQueryRepository {
    public List<Member> findAllMember();
}
