# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                        bigint not null,
  last_edited               timestamp,
  created                   timestamp,
  permissons                varchar(255),
  title                     varchar(255),
  content                   TEXT,
  preview                   varchar(255),
  cover                     varchar(255),
  constraint pk_article primary key (id))
;

create table comment (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  content                   varchar(255),
  created                   timestamp,
  oo                        bigint,
  xx                        bigint,
  constraint pk_comment primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table notifications (
  id                        bigint not null,
  value                     varchar(255),
  status                    integer,
  type                      integer,
  constraint ck_notifications_status check (status in (0,1)),
  constraint ck_notifications_type check (type in (0,1,2)),
  constraint pk_notifications primary key (id))
;

create table section (
  id                        bigint not null,
  name                      varchar(255),
  alias                     varchar(255),
  constraint pk_section primary key (id))
;

create table security_role (
  id                        bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table tag (
  id                        bigint not null,
  name                      varchar(255),
  alias                     varchar(255),
  constraint pk_tag primary key (id))
;

create table temprorary_user (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  constraint pk_temprorary_user primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('PR','EV')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  last_login                timestamp,
  active                    boolean,
  email_validated           boolean,
  PROFILE_ID                bigint,
  constraint pk_users primary key (id))
;

create table user_permission (
  id                        bigint not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;

create table user_profile (
  id                        bigint not null,
  visibility                integer,
  constraint ck_user_profile_visibility check (visibility in (0,1,2)),
  constraint pk_user_profile primary key (id))
;


create table article_users (
  article_id                     bigint not null,
  users_id                       bigint not null,
  constraint pk_article_users primary key (article_id, users_id))
;

create table article_tag (
  article_id                     bigint not null,
  tag_id                         bigint not null,
  constraint pk_article_tag primary key (article_id, tag_id))
;

create table article_comment (
  article_id                     bigint not null,
  comment_id                     bigint not null,
  constraint pk_article_comment primary key (article_id, comment_id))
;

create table section_tag (
  section_id                     bigint not null,
  tag_id                         bigint not null,
  constraint pk_section_tag primary key (section_id, tag_id))
;

create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;

create table editor_section (
  users_id                       bigint not null,
  section_id                     bigint not null,
  constraint pk_editor_section primary key (users_id, section_id))
;

create table manager_section (
  users_id                       bigint not null,
  section_id                     bigint not null,
  constraint pk_manager_section primary key (users_id, section_id))
;

create table user_profile_users (
  user_profile_id                bigint not null,
  users_id                       bigint not null,
  constraint pk_user_profile_users primary key (user_profile_id, users_id))
;

create table user_profile_notifications (
  user_profile_id                bigint not null,
  notifications_id               bigint not null,
  constraint pk_user_profile_notifications primary key (user_profile_id, notifications_id))
;
create sequence article_seq;

create sequence comment_seq;

create sequence linked_account_seq;

create sequence notifications_seq;

create sequence section_seq;

create sequence security_role_seq;

create sequence tag_seq;

create sequence temprorary_user_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_permission_seq;

create sequence user_profile_seq;

alter table linked_account add constraint fk_linked_account_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_1 on linked_account (user_id);
alter table token_action add constraint fk_token_action_targetUser_2 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_2 on token_action (target_user_id);
alter table users add constraint fk_users_profile_3 foreign key (PROFILE_ID) references user_profile (id) on delete restrict on update restrict;
create index ix_users_profile_3 on users (PROFILE_ID);



alter table article_users add constraint fk_article_users_article_01 foreign key (article_id) references article (id) on delete restrict on update restrict;

alter table article_users add constraint fk_article_users_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table article_tag add constraint fk_article_tag_article_01 foreign key (article_id) references article (id) on delete restrict on update restrict;

alter table article_tag add constraint fk_article_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table article_comment add constraint fk_article_comment_article_01 foreign key (article_id) references article (id) on delete restrict on update restrict;

alter table article_comment add constraint fk_article_comment_comment_02 foreign key (comment_id) references comment (id) on delete restrict on update restrict;

alter table section_tag add constraint fk_section_tag_section_01 foreign key (section_id) references section (id) on delete restrict on update restrict;

alter table section_tag add constraint fk_section_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

alter table editor_section add constraint fk_editor_section_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table editor_section add constraint fk_editor_section_section_02 foreign key (section_id) references section (id) on delete restrict on update restrict;

alter table manager_section add constraint fk_manager_section_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table manager_section add constraint fk_manager_section_section_02 foreign key (section_id) references section (id) on delete restrict on update restrict;

alter table user_profile_users add constraint fk_user_profile_users_user_pr_01 foreign key (user_profile_id) references user_profile (id) on delete restrict on update restrict;

alter table user_profile_users add constraint fk_user_profile_users_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table user_profile_notifications add constraint fk_user_profile_notifications_01 foreign key (user_profile_id) references user_profile (id) on delete restrict on update restrict;

alter table user_profile_notifications add constraint fk_user_profile_notifications_02 foreign key (notifications_id) references notifications (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists article;

drop table if exists article_users;

drop table if exists article_tag;

drop table if exists article_comment;

drop table if exists comment;

drop table if exists linked_account;

drop table if exists notifications;

drop table if exists section;

drop table if exists section_tag;

drop table if exists security_role;

drop table if exists tag;

drop table if exists temprorary_user;

drop table if exists token_action;

drop table if exists users;

drop table if exists users_security_role;

drop table if exists users_user_permission;

drop table if exists editor_section;

drop table if exists manager_section;

drop table if exists user_permission;

drop table if exists user_profile;

drop table if exists user_profile_users;

drop table if exists user_profile_notifications;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists article_seq;

drop sequence if exists comment_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists notifications_seq;

drop sequence if exists section_seq;

drop sequence if exists security_role_seq;

drop sequence if exists tag_seq;

drop sequence if exists temprorary_user_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_permission_seq;

drop sequence if exists user_profile_seq;

