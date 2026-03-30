CREATE TABLE chat_rooms (
    id         BIGSERIAL PRIMARY KEY,
    room_key   VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE chat_room_members (
    room_id BIGINT NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
    user_id UUID   NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, user_id)
);