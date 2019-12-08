create table task
(
    id bigserial primary key,
    name character varying(128) not null,
    description character varying(1024),
    assigned character varying(128),
    completed boolean not null default false,
    priority integer not null default 0
)
