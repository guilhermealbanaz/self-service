CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (user_id, role)
); 