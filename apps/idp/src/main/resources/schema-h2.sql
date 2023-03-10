drop table if exists `users`;
drop table if exists `password`;
drop table if exists `password_history`;
drop table if exists `pseudonyms`;

drop sequence if exists `users_seq`;
create sequence `users_seq` start with 1 increment by 50;
create table `users`
(
    `id` bigint not null,
    `user_id` varchar not null unique,
    `password_id` bigint not null,
    `created_at` timestamp with time zone,
    `modified_at` timestamp with time zone,
    primary key (`id`)
);

drop sequence if exists `password_seq`;
create sequence `password_seq` start with 1 increment by 50;
create table `password`
(
    `id` bigint not null,
    `current_password` varchar not null,
    `created_at` timestamp with time zone,
    `modified_at` timestamp with time zone,
    primary key (`id`)
);

drop sequence if exists `password_history_seq`;
create sequence `password_history_seq` start with 1 increment by 50;
create table `password_history`
(
    `id` bigint not null,
    `old_value` varchar not null,
    `created_at` timestamp with time zone,
    `modified_at` timestamp with time zone,
    primary key (`id`)
);

drop sequence if exists `pseudonym_seq`;
create sequence `pseudonym_seq` start with 1 increment by 50;
create table `pseudonym`
(
    `id` bigint not null,
    `user_id` bigint not null,
    `pseudonym` varchar not null,
    `created_at` timestamp with time zone,
    `modified_at` timestamp with time zone,
    primary key (`id`)
);
alter table `pseudonym` add foreign key (`user_id`) references `users`(`id`);

drop table if exists `password_histories`;
create table `password_histories`
(
    `password_id` bigint not null,
    `histories_id` bigint not null
);

alter table `password_histories` add foreign key (`password_id`) references `password`(`id`);
alter table `password_histories` add foreign key (`histories_id`) references `password_history`(`id`);

alter table `users` add foreign key (`password_id`) references `password`(`id`);
