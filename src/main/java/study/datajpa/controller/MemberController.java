package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();

        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        //Web 확정 - 도메인 클래스 컨버터
        return member.getUsername();
    }


    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
        Page<Member> pageMember = memberRepository.findAll(pageable);
        // http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc 이런식으로 작성가능

        /** 디폴트값 변경방법1 --> application.yml에 추가
         * data: 
         *    web: 
         *      pageable: 
         *         default-page-size: 10 
         *         max-page-size: 2000
         */

        Page<MemberDto> pageMemberDto = pageMember.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return pageMemberDto;
    }
    
    @PostConstruct
    public void init() {
        for(int i= 0; i< 100; i++ ){
            memberRepository.save(new Member("user"+i, i));
        }
    }


}
