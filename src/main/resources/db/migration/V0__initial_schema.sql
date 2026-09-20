CREATE TABLE images (
    uuid UUID PRIMARY KEY,
    image_data BYTEA NOT NULL
);

CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    profile_picture_id UUID UNIQUE REFERENCES images(uuid),
    created_at DATE NOT NULL
);

CREATE TABLE user_roles (
    username VARCHAR(255) NOT NULL REFERENCES users(username),
    role VARCHAR(255)
);

CREATE TABLE categories (
    uuid UUID PRIMARY KEY,
    user_username VARCHAR(255) REFERENCES users(username),
    name VARCHAR(255)
);

CREATE TABLE study_sessions (
    uuid UUID PRIMARY KEY,
    user_id VARCHAR(255) REFERENCES users(username),
    category_id UUID REFERENCES categories(uuid),
    start_time TIMESTAMP(6) NOT NULL,
    end_time TIMESTAMP(6)
);
