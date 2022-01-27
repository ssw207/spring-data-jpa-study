package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메이클래스 컨버터가 화면에 넘어온 id값을 가지고 레파지토리를 통해 조회해옴
     * 권장하지 않음. 간단한경우에만 사용
     * 조회용으로만 사용해야함 트랜젝션상태에서 조회한게 아니기때문
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) { // 코드에한 설정이 글로벌 설정보다 우선함
        /**
         * 컨트롤러에 pageable이 있으면 PageRequest 객체를 만들어 세팅해줌
         * 디폴트 사이즈는 20
         *
         * http://localhost:8080/members?page=0&size=3&sort=id,desc&ort=username,desc 호출시
         * -> page는 0번 출력건수3 id기준 역순정렬 username 기준 역순정렬
         */
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    // 스프링이 구동한뒤 실행됨
    //@PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i,i));
        }
    }
}
