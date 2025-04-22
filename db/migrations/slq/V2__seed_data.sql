-- V2__seed_data.sql
-- Seed existing data into TMS tables

INSERT INTO public.users 
  (id, username, first_name, second_name, password, email, phone_number, status, role)
VALUES
  (1, 'admin', 'Alice',   'Anderson',
   'PBKDF2WithHmacSHA256:2048:1HySGOgi5kOqBfCXArQdN+EFmI/AfeBGpVU0wm5oaiA=:Y/u5NQUWRDD9QxaiGeNhdN1bjouuTE6VwmgNhh8EZPo=',
   'olalsamuel01@gmail.com', '0712345678', 'ACTIVE', 'ADMIN'),
  (2, 'bob',   'Bob',     'Brown',
   'hunter2',
   'bob@example.com',   '0723456789', 'ACTIVE', 'DRIVER');
