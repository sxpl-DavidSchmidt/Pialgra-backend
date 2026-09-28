-- Give existing users stable identifiers before changing their references.
ALTER TABLE users ADD COLUMN uuid UUID DEFAULT gen_random_uuid() NOT NULL;

ALTER TABLE categories ADD COLUMN user_id UUID;
UPDATE categories SET user_id = (
    SELECT users.uuid FROM users WHERE users.username = categories.user_username
);

ALTER TABLE study_sessions ADD COLUMN user_uuid UUID;
UPDATE study_sessions SET user_uuid = (
    SELECT users.uuid FROM users WHERE users.username = study_sessions.user_id
);

-- Dropping the old columns also removes their foreign-key constraints.
ALTER TABLE categories DROP COLUMN user_username;
ALTER TABLE study_sessions DROP COLUMN user_id;
ALTER TABLE study_sessions RENAME COLUMN user_uuid TO user_id;
ALTER TABLE users DROP CONSTRAINT users_pkey;

ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (uuid);
ALTER TABLE users ADD CONSTRAINT users_username_key UNIQUE (username);

ALTER TABLE categories ADD CONSTRAINT categories_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users(uuid);
ALTER TABLE study_sessions ADD CONSTRAINT study_sessions_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users(uuid);
