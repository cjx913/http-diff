-- DIFF
-- auto-generated definition
create table DIFF
(
    ID          INT auto_increment,
    MAPPING     VARCHAR(2048),
    DIFF        INT not null,
    CREATE_TIME DATETIME,
    END_TIME    DATETIME
);

comment on column DIFF.ID is '唯一标识';

comment on column DIFF.MAPPING is '请求分类标识';

comment on column DIFF.DIFF is '差异类型';

comment on column DIFF.CREATE_TIME is '开始时间';

comment on column DIFF.END_TIME is '结束时间';

create index DIFF_DIFF_INDEX
    on DIFF (DIFF desc);

create unique index DIFF_ID_UINDEX
    on DIFF (ID);

create index DIFF_MAPPING_INDEX
    on DIFF (MAPPING);

alter table DIFF
    add constraint DIFF_PK
        primary key (ID);

-- REQUEST
-- auto-generated definition
create table REQUEST
(
    ID           INT auto_increment,
    TYPE         VARCHAR(16)                 not null,
    METHOD       VARCHAR(16)   default 'GET' not null,
    PATH         VARCHAR(2048) default ''    not null,
    HEADERS      MEDIUMTEXT    default '',
    QUERY_PARAMS MEDIUMTEXT    default '',
    FORM_DATA    MEDIUMTEXT    default '',
    BODY         MEDIUMTEXT    default '',
    DIFF_ID      INT                         not null,
    constraint REQUEST_PK
        primary key (ID)
);

comment on table REQUEST is '请求信息表';

comment on column REQUEST.ID is '请求唯一标识';

comment on column REQUEST.TYPE is '请求类型:candidate/primary/secondary';

comment on column REQUEST.METHOD is '请求方法';

comment on column REQUEST.PATH is '请求路径';

comment on column REQUEST.DIFF_ID is '差异表ID';

create index REQUEST_DIFF_ID_INDEX
    on REQUEST (DIFF_ID desc);

create unique index REQUEST_INFO_ID_UINDEX
    on REQUEST (ID);

--RESPONSE
-- auto-generated definition
create table RESPONSE
(
    ID         INT auto_increment,
    REQUEST_ID INT not null,
    HEADERS    MEDIUMTEXT,
    BODY       MEDIUMTEXT,
    constraint RESPONSE_PK
        primary key (ID)
);

comment on column RESPONSE.ID is '唯一标识';

comment on column RESPONSE.REQUEST_ID is '响应对应请求的ID（REQUEST表ID）';

create unique index RESPONSE_ID_UINDEX
    on RESPONSE (ID);

-- RESPONSE_BODY_PARAMETER
create table RESPONSE_BODY_PARAMETER
(
    ID        INT auto_increment,
    MAPPING   VARCHAR(2048) not null,
    PATH      VARCHAR(2048),
    PARAMETER VARCHAR(64),
    DIFF      INT default 0 not null,
    constraint RESPONSE_BODY_PARAMETER_PK
        primary key (ID)
);

comment on table RESPONSE_BODY_PARAMETER is '响应体字段表';

comment on column RESPONSE_BODY_PARAMETER.MAPPING is '请求标识';

comment on column RESPONSE_BODY_PARAMETER.PATH is '字段路径';

comment on column RESPONSE_BODY_PARAMETER.PARAMETER is '字段名称';

create unique index RESPONSE_BODY_PARAMETER_ID_UINDEX
    on RESPONSE_BODY_PARAMETER (ID);






