-- V2__seed_data.sql
-- Seed existing data into TMS tables

COPY public.users (id, username, first_name, second_name, password, email, phone_number, status, role) FROM stdin;
1   admin  Alice   Anderson  PBKDF2WithHmacSHA256:2048:1HySGOgi5kOqBfCXArQdN+EFmI/AfeBGpVU0wm5oaiA=:Y/u5NQUWRDD9QxaiGeNhdN1bjouuTE6VwmgNhh8EZPo=   alice@example.com   0712345678  ACTIVE  ADMIN
2   bob    Bob     Brown     hunter2  bob@example.com     0723456789  ACTIVE  DRIVER
\.

COPY public.vehicle (id, plate_number, vehicle_model, vehicle_type, status) FROM stdin;
1   KAA123A   Toyota Hiace   Minibus   Active
2   KBB456B   Nissan Caravan Minivan  Active
\.

COPY public.transport_tasks (id, user_id, vehicle_id, title, notes, pickup_location, destination_location, task_status, assigned_at, completed_at, tracking_status) FROM stdin;
1   1   1   Morning Run   -   A    B   completed   2025-04-10 07:00:00   2025-04-10 09:00:00   DONE
\.

COPY public.driver_locations (id, driver_username, latitude, longitude, accuracy, logged_at) FROM stdin;
1   alice   -1.2921   36.8219   0.5   2025-04-10 07:05:00+00
\.
