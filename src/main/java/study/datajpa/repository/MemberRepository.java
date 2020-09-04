package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    // class com.sun.proxy.~~
    // 구현체가 없음에도 스프링이 구현클래스를 proxy로 만들어줌

    /**
     * JpaRepository<T, ID>
     * 
     * ㅡ쿼리 메소드 기능 제공 https://spring.io/projects/spring-data-jpa#learn
     * ㅡJPA Named쿼리 --> Entity에 작성
     *               / 실무에선 잘 쓰지않지만 로딩시점에 파싱을 먼저 해서 오류잡기에 좋음
     * ㅡ@Query 기능 / 실무에서 쓰임 + 로딩시점에서 파싱을 해서 오류잡기 굿
     * ㅡ파라미터 바인딩 / 위치기반X 이름기반O
     * ㅡ페이징
     * ㅡ벌크연산주의!
     * ㅡ@EntityGraph
     */

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();
    
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDtos();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    List<Member> findListByUsername(String username);   //컬렉션
    Member findMemberByUsername(String username);       //단건
    Optional<Member> findOptionalByUsername(String username);       //단건 Optional

    //페이징, 정령 파라미터
    //org.springframwork.data.domain.Sort : 정렬 기능 
    //org.springframwork.data.domain.Pageable : 페이징 기능 (내부에 sort 포함)


    // 주의: Page 는 1이 아닌 0부터 시작!
    Page<Member> findByAge(int age, Pageable pageable);
    // Slice<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용 안함

    @Modifying(clearAutomatically = true)  //필수! 없을 경우 오류 발생!
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
    //JPA 에서 벌크성연산은 DB에 직접 들어가므로 영속성 컨텍스트에는 반영되지 않음
    //따라서 메서드 적용 후엔 반드시 새로 데이터를 가져와야한다. (중요!! 조심!!)
    // clearAutomatically = true 옵션을 해주면 자동으로 위의 과정을 해줌!!


    //패치조인 방법 3가지

    // 방법1.
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 방법2.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 방법3.
    @Query("select m from Member m")
    @EntityGraph(attributePaths = { "team" })
    List<Member> findMemberEntityGraph();

    // 방법4
    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // JAP Hint(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트) & Lock
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // JAP Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}
