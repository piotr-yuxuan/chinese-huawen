-- This file might better be executed by user root. It deletes a database then
-- create it again.

drop database if exists huawen;
create database huawen;

use huawen;

create table `sinogram` ( -- caractère
	hash varchar(12) not null,
	ids varchar(256),
	primary key (hash)
);
