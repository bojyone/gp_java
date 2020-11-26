INSERT INTO users (email, is_moderator, name, password, reg_time)
            VALUES ('mail@mail.com', 0, 'Petrov Petr', 'Njfi54', '2020-10-25 00:00:00');
INSERT INTO users (email, is_moderator, name, password, reg_time)
            VALUES ('mail73@mail.com', 1, 'Ivanov Ivan', 'JHduef23', '2020-11-15 00:00:00');
INSERT INTO posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
            VALUES (1, 'ACCEPTED', 'Я написал первый пост!', '2020-10-25 10:00:00', 'Мой первый пост', 1, 2, 1);
INSERT INTO post_comments (text, time, post_id, user_id)
            VALUES ('Ура!', '2020-10-25 10:05:00', 1, 2);
INSERT INTO tags (name) VALUES ('Новый пост');
INSERT INTO tag2post (post_id, tag_id) VALUES (1, 1);