-- figure_preorder_ledger 스키마 (MariaDB, utf8mb4)
-- 설계 근거: doc/planning-and-db-design.md
-- Spring 자동 실행 대상이 아니다(classpath 루트가 아니라 db/ 아래). 수동으로 적용한다.
--
-- 공통 규칙
--  * 모든 테이블은 user_id(계정) 소유. 분류 테이블은 UNIQUE(user_id, id)를 두어 복합 FK의 대상이 된다.
--  * figure가 다른 사용자의 분류를 참조하지 못하도록 (user_id, xxx_id) 복합 FK로 묶는다.
--  * 분류를 상품이 쓰고 있으면 삭제 불가(RESTRICT), 상품이 지워지면 연결 행은 함께 삭제(CASCADE).
--  * "하위 최소 1개"와 기본 하위(기타/일반) 자동 생성은 DB가 강제하지 못해 앱에서 보장한다.

-- ---------------------------------------------------------------- 계정
CREATE TABLE users (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  login_id      VARCHAR(30)  NOT NULL COMMENT '로그인 아이디',
  email         VARCHAR(100) NOT NULL COMMENT '이메일',
  password_hash VARCHAR(100) NOT NULL COMMENT '비밀번호 해시(BCrypt)',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_login_id (login_id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB COMMENT='계정';

-- ---------------------------------------------------------------- 판매처 (채널 → 스토어)
CREATE TABLE shop_channel (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  name       VARCHAR(100) NOT NULL COMMENT '채널명 (네이버 스토어, 일반 스토어)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_shop_channel_user_name (user_id, name),
  UNIQUE KEY uk_shop_channel_user_id (user_id, id),
  CONSTRAINT fk_shop_channel_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='판매처 상위(채널)';

CREATE TABLE shop (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  channel_id BIGINT       NOT NULL COMMENT '상위 채널',
  name       VARCHAR(100) NOT NULL COMMENT '스토어명 (굿스마일 pw, 코믹스아트 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_shop_channel_name (channel_id, name),
  UNIQUE KEY uk_shop_user_id (user_id, id),
  CONSTRAINT fk_shop_channel FOREIGN KEY (user_id, channel_id) REFERENCES shop_channel (user_id, id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='판매처 하위(스토어)';

-- ---------------------------------------------------------------- 형태 (스케일/넨도로이드 → 푸치 등)
CREATE TABLE figure_type (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  name       VARCHAR(100) NOT NULL COMMENT '형태 상위명 (스케일, 넨도로이드)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_figure_type_user_name (user_id, name),
  UNIQUE KEY uk_figure_type_user_id (user_id, id),
  CONSTRAINT fk_figure_type_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='형태 상위';

CREATE TABLE figure_subtype (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  type_id    BIGINT       NOT NULL COMMENT '상위 형태',
  name       VARCHAR(100) NOT NULL COMMENT '형태 하위명 (푸치, 기타 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_figure_subtype_type_name (type_id, name),
  UNIQUE KEY uk_figure_subtype_user_id (user_id, id),
  CONSTRAINT fk_figure_subtype_type FOREIGN KEY (user_id, type_id) REFERENCES figure_type (user_id, id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='형태 하위';

-- ---------------------------------------------------------------- 시리즈 (굿스마일 라인업 → 팝업퍼레이드 등)
CREATE TABLE series_group (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  name       VARCHAR(100) NOT NULL COMMENT '시리즈 상위명 (굿스마일 라인업, 미쿠 시리즈)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_series_group_user_name (user_id, name),
  UNIQUE KEY uk_series_group_user_id (user_id, id),
  CONSTRAINT fk_series_group_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='시리즈 상위';

CREATE TABLE series (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  group_id   BIGINT       NOT NULL COMMENT '상위 시리즈',
  name       VARCHAR(100) NOT NULL COMMENT '시리즈 하위명 (팝업퍼레이드, 매지컬미라이 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_series_group_name (group_id, name),
  UNIQUE KEY uk_series_user_id (user_id, id),
  CONSTRAINT fk_series_group FOREIGN KEY (user_id, group_id) REFERENCES series_group (user_id, id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='시리즈 하위';

-- ---------------------------------------------------------------- 장르 → 캐릭터 (character는 예약어라 genre_character)
CREATE TABLE genre (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  name       VARCHAR(100) NOT NULL COMMENT '장르명 (보컬로이드, 바키 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_genre_user_name (user_id, name),
  UNIQUE KEY uk_genre_user_id (user_id, id),
  CONSTRAINT fk_genre_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='장르';

CREATE TABLE genre_character (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  genre_id   BIGINT       NOT NULL COMMENT '소속 장르',
  name       VARCHAR(100) NOT NULL COMMENT '캐릭터명 (하츠네 미쿠 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_genre_character_genre_name (genre_id, name),
  UNIQUE KEY uk_genre_character_user_id (user_id, id),
  CONSTRAINT fk_genre_character_genre FOREIGN KEY (user_id, genre_id) REFERENCES genre (user_id, id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='캐릭터(장르 하위)';

-- ---------------------------------------------------------------- 제조사 (단독)
CREATE TABLE maker (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id    BIGINT       NOT NULL COMMENT '소유 계정',
  name       VARCHAR(100) NOT NULL COMMENT '제조사명 (세가, 후류 등)',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_maker_user_name (user_id, name),
  UNIQUE KEY uk_maker_user_id (user_id, id),
  CONSTRAINT fk_maker_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='제조사';

-- ---------------------------------------------------------------- 상품
CREATE TABLE figure (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id              BIGINT       NOT NULL COMMENT '소유 계정',
  status               VARCHAR(20)  NOT NULL DEFAULT 'CART' COMMENT '상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료',
  payment_type         VARCHAR(10)  NULL COMMENT '결제 방식: FULL 전액, DEPOSIT 예약금+잔금 (미구매 이전에는 NULL, 미구매에서 넘길 때 확정)',
  name                 VARCHAR(255) NOT NULL COMMENT '상품명',
  delivery_month       DATE         NULL COMMENT '배송예정월 (해당 월 1일)',
  reservation_deadline DATE         NULL COMMENT '예약마감일',
  price                INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '개당 가격(원). 총액은 price × quantity',
  quantity             INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '수량',
  deposit_amount       INT UNSIGNED NULL COMMENT '결제한 예약금 총액 (예약금형만)',
  balance_due_date     DATE         NULL COMMENT '잔금 기한 (예약금형만)',
  deposit_paid_at      DATE         NULL COMMENT '예약금 결제일',
  paid_at              DATE         NULL COMMENT '전액 결제일',
  delivered_at         DATE         NULL COMMENT '배송완료일',
  subtype_id           BIGINT       NOT NULL COMMENT '형태 하위',
  shop_id              BIGINT       NOT NULL COMMENT '구매처(스토어)',
  maker_id             BIGINT       NOT NULL COMMENT '제조사',
  purchase_link        VARCHAR(2000) NULL COMMENT '구매 링크',
  thumbnail_url        VARCHAR(2000) NULL COMMENT '썸네일 외부 링크',
  description          MEDIUMTEXT   NULL COMMENT '상세 설명 (에디터 HTML)',
  created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_figure_user_id (user_id, id),
  KEY idx_figure_user_status_deadline (user_id, status, reservation_deadline),
  KEY idx_figure_user_delivery_month (user_id, delivery_month),
  CONSTRAINT fk_figure_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_figure_subtype FOREIGN KEY (user_id, subtype_id) REFERENCES figure_subtype (user_id, id),
  CONSTRAINT fk_figure_shop FOREIGN KEY (user_id, shop_id) REFERENCES shop (user_id, id),
  CONSTRAINT fk_figure_maker FOREIGN KEY (user_id, maker_id) REFERENCES maker (user_id, id),
  CONSTRAINT ck_figure_status CHECK (status IN ('CART', 'UNPURCHASED', 'RESERVED', 'PURCHASED', 'DELIVERED')),
  CONSTRAINT ck_figure_payment_type CHECK (payment_type IN ('FULL', 'DEPOSIT')),
  CONSTRAINT ck_figure_status_payment CHECK (
    (status IN ('CART', 'UNPURCHASED') AND payment_type IS NULL)
    OR (status = 'RESERVED' AND payment_type = 'DEPOSIT')
    OR (status IN ('PURCHASED', 'DELIVERED') AND payment_type IS NOT NULL)
  ),
  CONSTRAINT ck_figure_quantity CHECK (quantity >= 1)
) ENGINE=InnoDB COMMENT='피규어 상품';

-- ---------------------------------------------------------------- 연결 (N:M)
CREATE TABLE figure_series (
  user_id   BIGINT NOT NULL COMMENT '소유 계정',
  figure_id BIGINT NOT NULL COMMENT '상품',
  series_id BIGINT NOT NULL COMMENT '시리즈(하위)',
  PRIMARY KEY (figure_id, series_id),
  CONSTRAINT fk_figure_series_figure FOREIGN KEY (user_id, figure_id) REFERENCES figure (user_id, id) ON DELETE CASCADE,
  CONSTRAINT fk_figure_series_series FOREIGN KEY (user_id, series_id) REFERENCES series (user_id, id)
) ENGINE=InnoDB COMMENT='상품-시리즈 연결(N:M)';

CREATE TABLE figure_character (
  user_id      BIGINT NOT NULL COMMENT '소유 계정',
  figure_id    BIGINT NOT NULL COMMENT '상품',
  character_id BIGINT NOT NULL COMMENT '캐릭터',
  PRIMARY KEY (figure_id, character_id),
  CONSTRAINT fk_figure_character_figure FOREIGN KEY (user_id, figure_id) REFERENCES figure (user_id, id) ON DELETE CASCADE,
  CONSTRAINT fk_figure_character_character FOREIGN KEY (user_id, character_id) REFERENCES genre_character (user_id, id)
) ENGINE=InnoDB COMMENT='상품-캐릭터 연결(N:M)';

-- ---------------------------------------------------------------- 에디터 이미지
CREATE TABLE editor_image (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  user_id       BIGINT       NOT NULL COMMENT '소유 계정',
  figure_id     BIGINT       NULL COMMENT '연결된 상품 (등록 중에는 NULL)',
  file_path     VARCHAR(500) NOT NULL COMMENT '서버 저장 경로',
  original_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
  file_size     INT UNSIGNED NOT NULL COMMENT '파일 크기(byte)',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '업로드일 (연결 안 된 파일 정리 기준)',
  PRIMARY KEY (id),
  KEY idx_editor_image_user_figure (user_id, figure_id),
  CONSTRAINT fk_editor_image_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_editor_image_figure FOREIGN KEY (user_id, figure_id) REFERENCES figure (user_id, id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='에디터 본문 이미지';
