package jpabook.jpabook.service;

import jpabook.jpabook.domain.Member;
import jpabook.jpabook.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void signup() throws Exception {
        Member member = new Member();
        member.setName("kim");

        Long saveId = memberService.join(member);

        assertThat(member).isEqualTo(memberRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);


        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> {
                    memberService.join(member2);
                }).withMessageContaining("이미 존재하는 회원입니다.");
    }


}