INSERT INTO event (id, name, time, created_at, created_by)
VALUES (1,'RS-Main', '2022-12-22 03:43:00.000000', '2022-12-22 03:43:38.580000', 'shell'),
       (2,'Beginner', '2022-01-22 03:43:00.000000', '2022-12-22 03:43:38.580000', 'shell'),
       (3,'Star', '2022-05-22 03:43:00.000000', '2022-12-22 03:43:38.580000', 'shell');
SELECT setval('event_id_seq', (SELECT max(id) FROM event));

INSERT INTO users (id, user_name, first_name, last_name, role, status, registered_at)
VALUES ('1', 'test1', 'Партнер1', 'ФамилияПартнер1', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('2', 'test2', 'Партнер2', 'ФамилияПартнер2', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('3', 'test3', 'Партнер3', 'ФамилияПартнер3', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('4', 'test4', 'Партнер4', 'ФамилияПартнер4', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('5', 'test5', 'Партнер5', 'ФамилияПартнер5', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('6', 'test6', 'Партнер6', 'ФамилияПартнер6', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('7', 'test7', 'Партнер7', 'ФамилияПартнер7', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('8', 'test8', 'Партнер8', 'ФамилияПартнер8', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('9', 'test9', 'Партнер9', 'ФамилияПартнер9', 'LEADER', 'USER', '2022-12-22 03:43:38.580000'),
       ('10', 'test10', 'Партнерша1', 'ФамилияПартнерша1', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('11', 'test11', 'Партнерша2', 'ФамилияПартнерша2', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('12', 'test12', 'Партнерша3', 'ФамилияПартнерша3', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('13', 'test13', 'Партнерша4', 'ФамилияПартнерша4', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('14', 'test14', 'Партнерша5', 'ФамилияПартнерша5', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('15', 'test15', 'Партнерша6', 'ФамилияПартнерша6', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('16', 'test16', 'Партнерша7', 'ФамилияПартнерша7', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('17', 'test17', 'Партнерша8', 'ФамилияПартнерша8', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000'),
       ('18', 'test18', 'Партнерша9', 'ФамилияПартнерша9', 'FOLLOWER', 'USER', '2022-12-22 03:43:38.580000');

INSERT INTO users_event (user_id, event_id, partner_fullname, signed_at)
VALUES (1, 1, null, '2023-01-01 15:00:00.000000'),
       (2, 1, null, '2023-01-01 16:00:00.000000'),
       (3, 1, null, '2023-01-01 12:00:00.000000'),
       (4, 1, null, '2023-01-01 10:00:00.000000'),
       (5, 1, null, '2023-01-01 13:00:00.000000'),
       (10, 1, null, '2023-01-01 20:00:00.000000'),
       (11, 1, null, '2023-01-01 12:00:00.000000'),
       (12, 1, null, '2023-01-01 14:00:00.000000'),
       (13, 1, null, '2023-01-01 12:00:00.000000'),
       (14, 1, null, '2023-01-01 15:00:00.000000'),
       (15, 1, null, '2023-01-01 16:00:00.000000'),
       (16, 1, null, '2023-01-01 17:00:00.000000'),
       (6, 1, 'Партнерша1фамПара Партнерша1имяПара', '2023-01-01 15:00:00.000000'),
       (7, 1, 'Партнерша2фамПара Партнерша2имяПара', '2023-01-01 13:00:00.000000'),
       (17, 1, 'Партнер1фамПара Партнер1имяПара', '2023-01-01 10:00:00.000000'),
       (18, 1, 'Партнер2фамПара Партнер2имяПара', '2023-01-01 12:00:00.000000');
