package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberJapReapositoryTest {

    @Autowired MemberJpaReapository memberJpaReapository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaReapository.save(member);

        Member findMember = memberJpaReapository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaReapository.save(member1);
        memberJpaReapository.save(member2);

        Member findMember1 = memberJpaReapository.findById(member1.getId()).get();
        Member findMember2 = memberJpaReapository.findById(member2.getId()).get();

        // 단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!!!!!");

        // 리스트 조회 검증
        List<Member> all = memberJpaReapository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaReapository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaReapository.delete(member1);
        memberJpaReapository.delete(member2);

        long deletedCount = memberJpaReapository.count();
        assertThat(deletedCount).isEqualTo(0);


    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaReapository.save(m1);
        memberJpaReapository.save(m2);

        List<Member> result = memberJpaReapository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    
    @Test
    public void paging() {
        memberJpaReapository.save(new Member("member1", 10));
        memberJpaReapository.save(new Member("member2", 10));
        memberJpaReapository.save(new Member("member3", 10));
        memberJpaReapository.save(new Member("member4", 10));
        memberJpaReapository.save(new Member("member5", 10));

        int age = 10;
        int offset = 1;
        int limit = 3;

        List<Member> members = memberJpaReapository.findByPage(age, offset, limit);
        Long totalCount = memberJpaReapository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);

    }
    
    @Test
    public void bulkUpdate() {
        memberJpaReapository.save(new Member("member1", 10));
        memberJpaReapository.save(new Member("member2", 20));
        memberJpaReapository.save(new Member("member3", 30));
        memberJpaReapository.save(new Member("member4", 40));
        memberJpaReapository.save(new Member("member5", 50));

        int resultCount = memberJpaReapository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(4);
    }

    
}
