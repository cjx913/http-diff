-- auto-generated definition
create table  http_diff_result
(
    id                     int auto_increment comment '主键',
    version                varchar(64)   default '' not null comment '版本',
    `key`                  varchar(2048) default '' not null comment '匹配接口',
    denoise                int           default 0  not null comment '降噪服务个数',
    result                 tinyint       default 0  not null,
    expect_json_path_value json                     not null,
    candidate              json                     not null,
    masters                json                     not null,
    constraint http_diff_result_id_uindex
        unique (id)
);

alter table http_diff_result
    add primary key (id);

