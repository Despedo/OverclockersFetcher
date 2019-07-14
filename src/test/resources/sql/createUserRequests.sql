INSERT INTO request (application_user_id, request, created_datetime)
VALUES
((SELECT id FROM user_application WHERE email = 'test@gmail.com'), '1080ti', '2019-01-28 12:45:15.100000'),
((SELECT id FROM user_application WHERE email = 'test@gmail.com'), 'ddr3', '2019-01-26 14:12:10.100000');