INSERT INTO request (created_datetime, request, application_user_id)
VALUES
('2019-01-28 12:45:15.100000', '1080ti', (SELECT user_application_id FROM user_application WHERE email = 'test@gmail.com')),
('2019-01-26 14:12:10.100000', 'ddr3', (SELECT user_application_id FROM user_application WHERE email = 'test@gmail.com'));