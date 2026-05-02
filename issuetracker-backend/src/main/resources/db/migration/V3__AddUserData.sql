alter table users
    add column email varchar(255) unique not null;
alter table users
    add column company_name varchar(200);
alter table users
    add column profile_picture_path varchar(1024);