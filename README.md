# 과제 설명

# <br> <데이터구조>

mysql을 사용합니다. mysql의 username은 root, password는 빈스트링입니다.
username과 password를 로컬환경에 맞게 변경하시려면 application.properties 파일에서 하단 값을 수정해주세요.
- spring.datasource.username
- spring.datasource.password
  <br>
  실행을 위해서는 하단 쿼리를 실행해주세요.<br>

### 쿼리문 밑에 스키마/인덱스 설명도 있습니다.
<br><br>



### 테이블명: highlights(하이라이트)
- 컬럼
    - id: auto increment primary key입니다.
    - user_id: 사용자 식별자 key입니다. 요구사항에서 회원의 정보를 보는 기능이 없었고, 회원 정보 테이블은 시스템에 존재하지 않을 수도 있기에 users 테이블은 만들지 않았습니다.)
    - created_at: 데이터 생성시점입니다.
    - updated_at: 데이터의 마지막 수정시점입니다.
- 인덱스
    - 없음(전체적인 시스템을 만들었다면 user_id에 index를 걸 확률이 높았겠지만 해당 과제에서는 user_id로 highlights를 검색하지 않기 때문에 추가하지 않았습니다.)
- 테이블 생성 sql 문

~~~
create table liner_assignment.highlights
(
    id         bigint auto_increment
        primary key,
    created_at datetime not null,
    updated_at datetime not null,
    user_id    bigint   not null
);
~~~

### 테이블명: collections(컬렉션)
- 컬럼
    - id: auto increment primary key입니다.
    - hierarchy: 컬렉션의 계층을 뜻합니다. 부모가 없는 루트 컬렉션은 값으로 1일 가지고, 한 계층씩 내려갈 때마다 값이 1씩 증가합니다.
    - highlight_count: 자신과 하위 컬렉션에 연결된 모든 하이라이트들의 중복제거된 갯수를 저장하는 컬럼입니다. 조회할 때마다 계산하는 작업을 하지 않기 위해 비정규화 했습니다.
    - name: 컬렉션의 이름입니다.
    - user_id: 컬렉션의 주인인 사용자(user)의 id입니다.
    - parent_collection_id: 자신의 상위 컬렉션 즉 부모 컬렉션의 id를 저장해놓은 컬럼입니다.
    - created_at: 데이터 생성시점입니다.
    - updated_at: 데이터의 마지막 수정시점입니다.
- 인덱스
    - parent_collection_id: highlight_count를 계산하기 위해 자식 컬렉션을 검색할 때 쓰입니다.
    - (user_id, hierarchy): user_id와 hierarchy 두 컬럼에 대한 복합 인덱스입니다.
      <br>컬렉션 리스트를 bfs 순으로 검색할 때 쓰이는 인덱스입니다. user_id가 hierarchy 보다 순서적으로 앞에 있는 이유는 사용자 구분 없이 계층으로 컬렉션을 검색할 일은 없지만, user_id로만 검색할 일을 충분히 있을 수 있기 때문입니다.

~~~
create table liner_assignment.collections
(
    id                   bigint auto_increment
        primary key,
    hierarchy            int          not null,
    highlight_count      int          not null,
    name                 varchar(255) not null,
    user_id              bigint       not null,
    parent_collection_id bigint       null,
    created_at           datetime     not null,
    updated_at           datetime     not null,
    constraint collections_collections_id_fk
        foreign key (parent_collection_id) references liner_assignment.collections (id)
);

create index collections_user_id_hierarchy_index
    on liner_assignment.collections (user_id, hierarchy);

~~~

### 테이블명: highlight_colletion_maps(하이라이트-컬렉션 연결정보)
- 컬럼
    - id: auto increment primary key입니다.
    - collection_id: 연결된 컬렉션의 id(pk)입니다. FK입니다.
    - highlight_id: 연결된 하이라이트의 id(pk)입니다. FK입니다.
    - created_at: 데이터 생성시점입니다.
    - updated_at: 데이터의 마지막 수정시점입니다.
- 인덱스
    - collection_id: FK 설정으로 인해 인덱스도 존재합니다.
    - highlight_id: FK 설정으로 인해 인덱스도 존재합니다.
    - (highlight_id, collection_id): highligh_id와 collection_id 두 컬럼의 복합인덱스입니다. 실제 해당 인덱스로 검색을 하지는 않지만 같은 컬렉션에 같은 하이라이트가 여러개 연결되는 것을 방지하기 위해 생성한 uniq 인덱스입니다.
      코드레벨에서는 이미 데이터가 존재하면 에러를 발생시키지 않고 넘어가도록 처리해두었습니다(그게 사용자 경험이 자연스럽다고 생각했습니다.).
~~~
create table liner_assignment.highlight_collection_maps
(
    id            bigint auto_increment
        primary key,
    created_at    datetime not null,
    updated_at    datetime not null,
    collection_id bigint   not null,
    highlight_id  bigint   not null,
    constraint highlight_id_collection_id_uindex
        unique (highlight_id, collection_id),
    constraint collections_id_fk
        foreign key (collection_id) references liner_assignment.collections (id),
    constraint highlights_id_fk
        foreign key (highlight_id) references liner_assignment.highlights (id)
);
~~~


# <API 명세> <br>

모든 api 는 공통 리스펀스(CommonResponse)로 감싸져있습니다. <br>
요구사항에 명시된 데이터는 data 필드에 있습니다.
~~~
{
	"result": "SUCCESS",
	"data": null,
	"message": null,
	"errorCode": null
}
~~~

### 하이라이트-컬렉션 연결 API
- method: POST
- url: localhost:8080/highlights/{highlightId}/connect
- body: { "collectionIdList": [] }
- response:
  ~~~
  {
	"result": "SUCCESS",
	"data": null,
	"message": null,
	"errorCode": null
  }
  ~~~

### 컬렉션 조회 API
- method: GET
- url: localhost:8080/collections
- paramter:
    - userId(필수값): 사용자id
    - cursorCollectionId(선택값): 조회 시작 컬렉션 id
    - size(선택값): 조회할 컬렉션의 수
- response:
  ~~~
  {
	"result": "SUCCESS",
	"data": [
		{
			"collectionId": 4,
			"name": "user2꺼",
			"highlightCount": 0,
			"children": [
				{
					"collectionId": 5,
					"name": "user2꺼",
					"highlightCount": 0,
					"children": [
						{
							"collectionId": 6,
							"name": "user2꺼",
							"highlightCount": 0,
							"children": []
						}
					]
				},
				{
					"collectionId": 22,
					"name": "user2꺼",
					"highlightCount": 0,
					"children": []
				}
			]
		},
		{
			"collectionId": 13,
			"name": "user2꺼",
			"highlightCount": 0,
			"children": []
		}
	],
	"message": null,
	"errorCode": null
  }
  ~~~



# <br> <프로젝트 레이어 구조>

### 1. interfaces
사용자와 상호 작용하는 책임을 가진 클래스가 존재합니다.<br>
이 프로젝트에서는 사용자의 입력을 받고 정보를 리턴하는 컨트롤러가 포함됩니다.
### 2. application
수행 작업을 지시하고, 도메인 객체에게 작업 지시합니다. 상세 도메인 로직은 가지고 있지 않으며,<br>
트랜잭션으로 묶여있지 있지 않습니다.
### 3. domain
실제로 도메인 로직을 처리하는 클래스들이 포함되어 있습니다. 구현하고자 하는 도메인의 규칙, 정보, 상태를 표현합니다. <br>
해당 레이어에 속한 코드를 읽으면 비즈니스의 흐름을 이해할 수 있는 핵심 레이어입니다.<br>
interface를 통한 추상화가 많이 되어 있고, 기술적 세부사항은 infrastructure 레이어에 위임합니다.
### 4. infrastructure
domain 레이어에 선언된 interface의 실제 상세 구현들이 위치합니다.<br>
해당 레이어에 실제 구현이 위치하기 떄문에 세부 사용 기술이 변경되어도 domain 레이어는 그대로 유지할 수 있습니다.
### 5. common
공통적으로 사용되는 exception 또는 유틸성 클래스가 존재합니다.

# <br> 프로젝트 구조 특징
domain 레이어는 주요 로직은 모두 추상화되어 있고, 실제 구현은 infrastructure에 있기 때문에 <br>
domain는 상세 기술에 구애받지 않고, 순수하게 도메인 관련된 코드를 담고 있습니다. <br>
때문에 domain 레이어의 코드를 살펴보면 빠르게 비즈니스 흐름을 파악할 수 있습니다.
