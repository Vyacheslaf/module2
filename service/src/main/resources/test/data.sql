insert into tag(name) values ('tag1');
insert into tag(name) values ('tag2');
insert into tag(name) values ('tag3');

insert into gift_certificate values (default, 'name1', 'description1', 111, 11, '2001-01-01 01:01:01.001', '2001-01-01 01:01:01.001');
insert into gift_certificate values (default, 'name2', 'description2', 222, 22, '2002-02-02 02:02:02.002', '2002-02-02 02:02:02.002');

insert into gift_certificate_tag values (1, 1);
insert into gift_certificate_tag values (1, 3);
insert into gift_certificate_tag values (2, 2);
insert into gift_certificate_tag values (2, 3);
