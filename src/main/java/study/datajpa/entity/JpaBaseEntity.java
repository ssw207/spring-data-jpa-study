package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass // 상속 받은테이블에서 속성을 사용가능함
public class JpaBaseEntity {
    @Column(updatable = false) // 수정불가능하도록
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // 영속화 하기전에(저장) 실행됨
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; // 실무적으로 쿼리하기 편하게 값을 체운다.
    }

    @PreUpdate // 영속화 하기전에 실행됨
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
}
