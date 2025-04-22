-- V1__baseline_schema.sql
-- Baseline of existing TMS schema (version 1)

-- Table: users
CREATE TABLE public.users (
    id            INTEGER      NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    first_name    VARCHAR(50),
    second_name   VARCHAR(50),
    password      TEXT         NOT NULL,
    email         VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(20),
    status        VARCHAR(20)  DEFAULT 'ACTIVE',
    role          VARCHAR(20)  NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN','DRIVER','USER')),
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_phone_number_key UNIQUE (phone_number),
    CONSTRAINT users_username_key UNIQUE (username)
);

-- Sequence: users_id_seq
CREATE SEQUENCE public.users_id_seq
    AS INTEGER
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
ALTER TABLE public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq');

-- Table: vehicle
CREATE TABLE public.vehicle (
    id            INTEGER      NOT NULL,
    plate_number  VARCHAR(50)  NOT NULL,
    vehicle_model VARCHAR(100) NOT NULL,
    vehicle_type  VARCHAR(100) NOT NULL,
    status        VARCHAR(50)  DEFAULT 'Active',
    CONSTRAINT vehicle_pkey PRIMARY KEY (id),
    CONSTRAINT vehicle_plate_number_key UNIQUE (plate_number)
);

CREATE SEQUENCE public.vehicle_id_seq
    AS INTEGER
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.vehicle_id_seq OWNED BY public.vehicle.id;
ALTER TABLE public.vehicle ALTER COLUMN id SET DEFAULT nextval('public.vehicle_id_seq');

-- Table: transport_tasks
CREATE TABLE public.transport_tasks (
    id              INTEGER      NOT NULL,
    user_id         INTEGER      NOT NULL REFERENCES public.users(id),
    vehicle_id      INTEGER      NOT NULL REFERENCES public.vehicle(id),
    title           VARCHAR(255) NOT NULL,
    notes           TEXT,
    pickup_location VARCHAR(255),
    destination_location VARCHAR(255),
    task_status     VARCHAR(50)  DEFAULT 'pending',
    assigned_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP,
    tracking_status VARCHAR(50)  DEFAULT 'PENDING',
    CONSTRAINT transport_tasks_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE public.transport_tasks_id_seq
    AS INTEGER
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.transport_tasks_id_seq OWNED BY public.transport_tasks.id;
ALTER TABLE public.transport_tasks ALTER COLUMN id SET DEFAULT nextval('public.transport_tasks_id_seq');

-- Table: driver_locations
CREATE TABLE public.driver_locations (
    id               BIGINT      NOT NULL,
    driver_username  VARCHAR(100) NOT NULL,
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    accuracy         REAL,
    logged_at        TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT driver_locations_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE public.driver_locations_id_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.driver_locations_id_seq OWNED BY public.driver_locations.id;
ALTER TABLE public.driver_locations ALTER COLUMN id SET DEFAULT nextval('public.driver_locations_id_seq');

CREATE INDEX idx_driver_locations_time ON public.driver_locations (logged_at);
CREATE INDEX idx_driver_locations_username ON public.driver_locations (driver_username);
