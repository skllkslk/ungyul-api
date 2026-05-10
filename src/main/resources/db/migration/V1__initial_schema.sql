CREATE TABLE users
(
    id              BIGSERIAL PRIMARY KEY,
    social_provider VARCHAR(50),
    social_id       VARCHAR(255),
    email           VARCHAR(255),
    nickname        VARCHAR(100),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE birth_profiles
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT  NOT NULL REFERENCES users (id),
    birth_date DATE,
    birth_time TIME,
    is_lunar   BOOLEAN NOT NULL DEFAULT FALSE,
    gender     VARCHAR(10),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (user_id)
);

CREATE TABLE saju_profiles
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users (id),
    birth_info_id BIGINT REFERENCES birth_profiles (id),
    day_master    VARCHAR(50),
    profile_text  TEXT,
    saju_raw_json TEXT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    UNIQUE (user_id)
);

CREATE TABLE daily_reports
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users (id),
    report_date DATE   NOT NULL,
    mood        VARCHAR(50),
    content     TEXT,
    created_at  TIMESTAMP,
    UNIQUE (user_id, report_date)
);

CREATE TABLE insight_reports
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT      NOT NULL REFERENCES users (id),
    insight_type       VARCHAR(20) NOT NULL,
    period_start_date  DATE,
    period_end_date    DATE,
    title              VARCHAR(255),
    summary            TEXT,
    interpretation     TEXT,
    action_suggestions TEXT,
    created_at         TIMESTAMP
);
