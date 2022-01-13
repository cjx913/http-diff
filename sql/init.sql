-- auto-generated definition
create table http_diff_result
(
    id                     int auto_increment comment '主键',
    version                varchar(64)   default '' not null comment '版本',
    `key`                  varchar(2048) default '' not null comment '匹配接口',
    denoise                int           default 0  not null comment '降噪服务个数',
    result                 tinyint       default 0  not null,
    expect_json_path_value json                     not null,
    candidate              json                     not null,
    masters                json                     not null,
    create_time            datetime                 null comment '创建时间',
    constraint http_diff_result_id_uindex
        unique (id)
);

create index http_diff_result_key_index
    on http_diff_result (`key`);

create index http_diff_result_version_index
    on http_diff_result (version);

alter table http_diff_result
    add primary key (id);

