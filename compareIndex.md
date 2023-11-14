# index가 있을 때와 없을 때 쿼리 비교

## 데이터 3백만 건

<img src="/img/p1_4.png"><br/>

memberId는 하나<br/>
contents는 랜덤 문자열<br/>
createdDate는 1998-01-01 ~ 2011-02-03 사이에 고르게 분포

## 인덱스 추가

```
create index POST__index_member_id
on POST (memberId);

create index POST__index_created_date
on POST (createdDate);

create index POST__index_member_id_created_date
on POST (memberId, createdDate);
```

### 인덱스 없이 쿼리

```
select createdDate, memberId, count(id)
from post
where memberId = 3 and createdDate between '1998-01-01' and '2011-02-04'
group by memberId, createdDate;
```

약 2s 소요

### 인덱스 사용 쿼리

1.

```
select createdDate, memberId, count(id)
from post use index (POST__index_member_id)
where memberId = 3 and createdDate between '1998-01-01' and '2011-02-04'
group by memberId, createdDate;
```

약 10s 소요</br></br>
왜?
explain 걸어서 확인해보면 Type에 ref가 써있음.</br>
즉 인덱스 비교가 되었다는 것.</br>
근데 memberId의 값이 하나기 때문에 걸러낼 수 있는 값이 없음.</br>
즉 모든 데이터에 인덱스도 보고 테이블도 봐야함. 그래서 더 오래 걸림</br></br>

즉 같은 쿼리라도 해당 인덱스의 분포도에 따라 쿼리가 훨씬 느려질 수 있다.</br>

2.

```
select createdDate, memberId, count(id)
from post use index(POST__index_created_date)
where memberId = 3 and createdDate between '1998-01-01' and '2011-02-04'
group by memberId, createdDate;
```

1번 쿼리보다 빠름.</br>
memberId가 3일 때가 memberId 1일 때보다 더 빠름 왜?</br>
3의 식별값이 더 많기 때문</br>

3.

```
select createdDate, memberId, count(id)
from post use index (POST__index_member_id_created_date)
where memberId = 3 and createdDate between '1998-01-01' and '2011-02-04'
group by memberId, createdDate;
```

복합 인덱스 사용. memberId로 먼저 탐색하고 중복일 때 createdDate로 탐색 범위를 좁혀나감.</br>
위 두 쿼리보다 훨씬 빠름.</br></br>

같은 인덱스여도 파라미터 데이터의 분포도에 따라 성능이 많이 달라짐.</br>
인덱스 사용 시 데이터 분포도, 어떤 컬럼이 group by나 order by에 들어가는지 등등 같이 고려해야 함.</br>
어쨌든 인덱스를 잘 적용하면 성능을 많이 향상할 수 있지만 잘못 사용하면 성능이 더 떨어질 수도 있다.</br></br>

위는 강제적으로 원한는 인덱스를 적용했지만 아무것도 명시하지 않으면 옵티마이저가 알아서 선택함. 근데 이게 안 좋은 걸 선택할 수도 있음.</br>
항상 explain 문으로 어떤 인덱스를 선택하는지 확인.</br>
