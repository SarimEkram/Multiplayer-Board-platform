-- Schema for the CSV-to-PostgreSQL persistence migration (specs/001-postgres-migration).
-- Written in ANSI-portable SQL so it runs unchanged against both PostgreSQL (dev/integration)
-- and H2 in PostgreSQL-compatibility mode (unit tests) — see research.md §3.
--
-- No FOREIGN KEY constraints and no UNIQUE constraints beyond the primary keys: the current
-- CSV-backed implementation enforces neither (see research.md §8 and data-model.md), and this
-- migration must not introduce new integrity rules that change existing behavior.

CREATE TABLE users (
    user_id        BIGINT PRIMARY KEY,
    username       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    win_ratio      DOUBLE PRECISION NOT NULL,
    level          INTEGER NOT NULL,
    online_status  BOOLEAN NOT NULL
);

-- Supports UserDatabase.getUserByUsername / getUserByEmail. Not UNIQUE — see note above.
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

CREATE TABLE players (
    user_id  BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    level    INTEGER NOT NULL
);

CREATE INDEX idx_players_username ON players (username);

CREATE TABLE player_game_stats (
    user_id      BIGINT NOT NULL,
    game_type    VARCHAR(32) NOT NULL,
    wins         INTEGER NOT NULL,
    losses       INTEGER NOT NULL,
    mmr          INTEGER NOT NULL,
    win_ratio    DOUBLE PRECISION NOT NULL,
    rank_tier    VARCHAR(32) NOT NULL,
    game_signal  INTEGER NOT NULL,
    PRIMARY KEY (user_id, game_type)
);
-- No separate index on (user_id) alone: the composite primary key above already provides an
-- index usable for lookups by user_id (its leftmost column), per research.md §9.

CREATE TABLE friendships (
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE game_history (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    game_type      VARCHAR(32) NOT NULL,
    player_one_id  BIGINT NOT NULL,
    player_two_id  BIGINT NOT NULL,
    winner_id      BIGINT,
    played_at      TIMESTAMP NOT NULL
);
