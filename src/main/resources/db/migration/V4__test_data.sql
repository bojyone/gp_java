INSERT INTO users (email, is_moderator, name, password, reg_time)
            VALUES ('mail123@mail.com', 0, 'Sidorov Sidor', 'Gsjhdsejr1', '2020-10-28 00:00:00');
INSERT INTO users (email, is_moderator, name, password, reg_time)
            VALUES ('mail738374@mail.com', 1, 'Alex Alex', 'wkdjYtwvs', '2020-11-19 00:00:00');
INSERT INTO posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
            VALUES (1, 'ACCEPTED', 'Im fine, friends', '2020-10-30 10:00:00', 'Fine', 3, 2, 3);
INSERT INTO post_comments (text, time, post_id, user_id)
            VALUES ('Ура!', '2020-10-30 10:05:00', 2, 4);
INSERT INTO post_comments (text, time, post_id, user_id)
            VALUES ('Good', '2020-10-30 10:07:00', 2, 1);
INSERT INTO posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
            VALUES (1, 'ACCEPTED', 'I like jam!!!', '2020-10-26 10:00:00', 'Jam', 1, 2, 1);
INSERT INTO post_comments (text, time, post_id, user_id)
            VALUES ('Ура!', '2020-10-30 10:05:00', 3, 2);
INSERT INTO post_votes (user_id, post_id, time, value)
            VALUES (1, 2, '2020-10-30 10:06:00', 1);
INSERT INTO post_votes (user_id, post_id, time, value)
            VALUES (4, 2, '2020-10-30 10:06:00', 0);
INSERT INTO post_votes (user_id, post_id, time, value)
            VALUES (4, 1, '2020-10-30 10:06:00', 0);