package jpabook.jpabook.api;

import jpabook.jpabook.domain.Member;
import jpabook.jpabook.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /*
        V1 : Member 엔티티를 직접 받음.
        문제점
        - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
        - 엔티티에 api 검증을 위한 로직이 들어가게 된다. ( @NotEmpty... 등 )
        - 엔티티가 변하게 되면 API 스펙이 변하게 된다.

        결론
        - 엔티티를 api 스펙에 노출하지 말 것!
        - 엔티티를 직접 사용하는 것이 아닌, DTO를 사용하자.
        - DTO를 사용하면, 엔티티가 변경이 되어도 큰 문제가 발생하지 않는다.
    */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*
        V2 : 요청 값으로 엔티티 대신 DTO를 받음
        - 엔티티를 직접 bind하지말고 별도의 DTO를 만들어서 사용하자.
        - DTO -> Data Transfer Object
        - 엔티티가 변해도 API 스펙이 변하지 않는다.
    */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.name);

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*
       회원 수정 API
       - update시에는 PUT이나 PATCH Method를 사용
       - PUT은 전체 수정
       - PATCH는 부분 수정
       - dirty checking(변경감지)을 이용하자.
    */
    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /*
        조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한 문제점
        - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
        - 기본적으로 엔티티의 모든 값이 노출된다.
        - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
        - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데,
          한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
        - 엔티티가 변경되면 API 스펙이 변한다.
        - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)

        결론
        - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.

        안 좋은 버전, 모든 엔티티가 노출
        @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!
    */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    /*
        조회 V2 : 응답을 DTO로 반환
    */
    @GetMapping("/api/v2/members")
    public Result<List<MemberDto>> membersV2(){
        List<Member> members = memberService.findMembers();
        List<MemberDto> collect = members.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result<>(collect);
    }



    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
