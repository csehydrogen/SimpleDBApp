create table if not exists university (
	uid int primary key,
	name varchar(128),
	capacity int,
	ugroup enum('A', 'B', 'C'),
	weight float,
    applied int
);

create table if not exists student
(
	sid int primary key,
    name varchar(20),
    csat_score int,
    school_score int
);

create table if not exists apply
(
	uid int,
    sid int,
    index (uid),
    index (sid)
);