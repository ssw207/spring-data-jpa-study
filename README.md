# spring-data-jpa-study
인프런 실전! 스프링 데이터 JPA 예제

## 쿼리 메소드
- 스프링 데이터 jpa는 조회결과가 없으면 null을 반환한다.
- Optional을 지원한다. (spring data jpa 2.0부터)
- findById는 기본적으로 Optional을 반환하고 그외 직접만든 findBy컬럼명은 null을 반환한다.
- 컬랙견 조회시 조회결과가 없으면 빈 컬력션이 반환된다
```java
// spring data jpa는 단일 조회결과가 없으면 결과를 null을 리턴한다
Item item = itemRepository.findByName("name");
assertThat(item).isNull();

// Optional을 지원한다 (spring data jpa 2.0부터)
Optional<Item> name = itemRepository.findOptionalByName("name");
assertThat(name).isEqualTo(Optional.empty());

// spring data jpa는 다중 조회결과가 없으면 빈 리스트를 리턴한다. 컬랙션을 jqpl 파라미터로 이용시 in으로 표시한다
List<Item> items = itemRepository.findAllByNames(List.of("name1", "name2"));
assertThat(items).isNotNull();
```

## 페이징
- repository 인터페이스에 Pageable인터페이스를 구현한 PageRequest를 파라미터로 전달하면 자동으로 페이징 처리가 된다.
- 반환타입 Page는 페이저 번호가 필요한경우 (카운트 쿼리가 필요한 경우) 사용
- 반환타입 Slice는 다음 페이지가 있는지만 확인하고 싶은경우 사용 (카운트 쿼리가 필요없는경우)
  - limit수 + 1 개만큼 조회해서 다음페이지가 있는지 체크한다 
- 반환타입 List는 페이지 번호, 다음페이지를 알 필요없을때 사용

### 일반적인 페이징
```java
// 페이지는 0부터, 3개를 가져오고 유저명 역순정렬
PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

//Page는 count 쿼리를 추가로 실행한다
Page<Member> page = memberRepository.findByAge(age, pageRequest);

//Slice 사용시 count 쿼리가 나가지 않고 다음 페이지가 있는지 여부만 확인가능하다. (limit +1 )
Slice<Member> page = memberRepository.findByAge(age, pageRequest);
```

### *페이징 최적화
- 쿼리가 복잡하면 (left jon 여러번을 사용) Page를 사용하면 본 쿼리와 같은 쿼리로 count 쿼리를 날리기 때문에 성능이 떨어진다
- 이때 `@Query`의 `ocuntQuery` 속성을 사용하여 별도의 count 쿼리를 작성하면 된다
```java
@Query(value = "select m from Member m join m.team t",
            countQuery = "select count(m.username) from Member m") // count쿼리는 join을 사용하지않아 빠르다
    Page<Member> findQueryByAge(int age, Pageable pageable);
```

### *페이징 조회결과를 DTO로 변환
```java
Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
```

## 벌크 연산
- 여러건을 수정,삭제할때 벌크연산을 사용한다.
- 벌크연산은 영속성 컨텍스트를 무시하기 하기 때문에 이미 영속화된 엔티티를 벌크연산으로 수정하면 같은 트렌젝션에서 조회시 벌크연산의 결과가 적용되지 않는다.
- clearAutomatically = true 속성을 주면 메서드 실행후 자동으로 영속성 컨텍스트를 초기화 한다.
```java
@Modifying(clearAutomatically = true) // jpa executeUpdate 실행.
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

## *@EntityGraph 
- 스프링 데이터 jpa에서 간단한 쿼리에서 패치 조인을 편하게 사용하고 싶을 때 사용한다. 복잡한 쿼리에서 사용하지 않는걸 추천.
- @EntityGraph(attributePaths = {"team"}) : 내부적으로 team을 패치조인한다.
- `@Query("select m from Member m join fetch m.team")` 과 같다
```java
@EntityGraph(attributePaths = {"team"}) // 내부적으로 패치조인을 사용한다
List<Member> findEntityGraphByUsername(@Param("username") String username);
```

## 힌트 & 락
- @QueryHints는 DB의 힌트가 아니라 하이버네이트의 힌트이다.
- @QueryHints는 readOnly를 설정하면 영속성 컨텍스트에서 캐싱하지 않음 즉 더티체킹이 불가능
    ```java
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
    ```
- @Lock을 이용하면 데이터베이스의 락을 사용할 수 있다.
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
List<Member> findLockByUsername(String username);
```
```sql
select * 
from
    member member0_ 
where
    member0_.username=? for update
```

## 사용자 정의 repository
- 핵심 비즈니스와 연관된 로직이지만 스프링 데이터 JPA로 해결하기 힘든경우 커스텀 repository를 만든후 연결해 사용한다.
- 특정 화면에 종속되 기능은 별도에 repository를 만들어 사용할것

## Auditing
등록, 수정일을 자동으로 입력함
1. @EnableJpaAuditing 어노테이션을 스프링부트 설정 클래스에 적용
2. @EntityListeners(AuditingEntityListener.class) 어노테이션을 적용하려는 엔티티에 추가 
   -  @CreatedDate
      @LastModifiedDate
      @CreatedBy
      @LastModifiedBy
    ```java
    @EntityListeners(AuditingEntityListener.class)
    @MappedSuperclass
    @Getter
    public class BaseEntity {
        // 등록일, 수정일
        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdDate;
        @LastModifiedDate
        private LocalDateTime lastModifiedDate;
    
        // 등록자, 수정자
        @CreatedBy
        @Column(updatable = false)
        private String createdBy;
        @LastModifiedBy
        private String lastModifiedBy;
    }
    ```
3. 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록. 세션등 사용자 아이디를 조회해 리턴 해야한다.
    ```java
    @Bean
    public AuditorAware<String> auditorProvider() {
    return () -> Optional.of(UUID.randomUUID().toString());
    }
    ```
   
- 실무 추천 방식. 수정자 등록자는 필요 없을 수 있으므로 클래스를 분리한다.
```java
public class BaseTimeEntity {
     @CreatedDate
     @Column(updatable = false)
     private LocalDateTime createdDate;
     @LastModifiedDate
     private LocalDateTime lastModifiedDate;
}

public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}
```

## Web확장 - 페이징과 정렬
- 페이징을 기본적으로 페이지 0부터 시작하는데 아래 설정을 true로하면 1부터 시작하게 할수 있다.
    ```properties
    spring
      data:
        web:
          pageable:
            default-page-size: 10
            max-page-size: 2000
            one-indexed-parameters: true
    ```
- 다만 문제는 아래 설정이 단순 page 파라미터값을 -1로 해서 전달 한다는 점이다.
- Page 객체에는 page 번호와 연관된 값들이 이 있는데 이 값들은 페이지0이 첫페이지를 전제로 동작해서 불일치가 발생할 수 있다.
```java

// 위 설정이 켜져있으면 page 파라미터를 0 or 1로 호출시 첫 페이지가 조회된다.
PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));
Page<Member> page = memberRepository.findByAge(age, pageRequest);

//then
List<Member> content = page.getContent(); // 조회 데이터수
        
assertEquals(page.getNumber(), 0); // page 파라미터를 1로 전달했지만 pageable 속성의 page값은 여전히 0이다
```

## 스프링 데이터 JPA 구현체 분석
- save()시 entityManager.isNew()로 신규 엔티티인지 확인한뒤 persist() 또는 merge()를 호출한다.
  - 기본적으로 id가 null or 0(원시타입) 이면 신규로 판단한다.
- pk 자동생성을 안하면 save시점에 id가 앖기 때문에 merge()가 호출되는데 merge()는 select를 먼저 하고 없으면 insert, 있으면 update를 하기 때문에 성능이 떨어진다.
- Entity가 Persistable를 구현하면 isNew 조건을 변경 할 수 있다.
    ```java
    @EntityListeners(AuditingEntityListener.class)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class Item implements Persistable<String> {
     @Id
     private String id;
         @CreatedDate
         private LocalDateTime createdDate;
         
         public Item(String id) {
             this.id = id;
         }
         @Override
         public String getId() {
            return id;
         }
         @Override
         public boolean isNew() {
            return createdDate == null;
         }
    }
    ```
