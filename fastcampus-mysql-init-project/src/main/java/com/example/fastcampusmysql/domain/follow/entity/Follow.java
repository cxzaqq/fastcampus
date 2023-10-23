package com.example.fastcampusmysql.domain.follow.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Follow {
    final private Long id;

    final private Long fromMemberId;

    final private Long toMemberId;

    final private LocalDateTime createdAt;

    @Builder
    public Follow(Long id, Long fromMemberId, Long toMemberId, LocalDateTime createdAt){
        this.id = id;
        this.fromMemberId = Objects.requireNonNull(fromMemberId);
        this.toMemberId = Objects.requireNonNull(toMemberId);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
/*
팔로우를 구현할 때 테이블에 식별자만 넣어 조인을 할 것인가 아니면
필요한 정보를 모두 넣을 것인가

데이터의 최신성을 보장해야 하는가? O => 
정규화 시 최신 데이터를 어떻게 가져올 것인가?
1. 조인
2. 조인 없이 쿼리를 한 번 더 날리기
3. 별도 데이터베이스를 이용
- 조인은 가능하면 미루는 것이 좋다. 왜? 조인 시 두 도메인의 강한 결합이 일어남
프로젝트 초기부터 이렇게 강한 결합이 생기면 추후에 유연성 있는 아키텍쳐나 시스템이 되기 힘들다.
*/