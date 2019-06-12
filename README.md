# mybatis-plus sharding demo
## a simple demo integrated spring-boot mybatis-plus and sharding-jdbc using java config

## to run this demo , you will need to modify org.lxy.demo.mp.sharding.config.ShardingConfig using your mysql url and so on
## and create your own table

table author is plain table
create TABLE t_author(guid VARCHAR(36),   name VARCHAR(36),   create_time timestamp,   update_time timestamp,   primary key(guid)   );

table book is split table
create TABLE t_book_201906(guid VARCHAR(36),   name VARCHAR(36),   create_time timestamp,   update_time timestamp,   primary key(guid)   );
create TABLE t_book_201907(guid VARCHAR(36),   name VARCHAR(36),   create_time timestamp,   update_time timestamp,   primary key(guid)   );
create TABLE t_book_201908(guid VARCHAR(36),   name VARCHAR(36),   create_time timestamp,   update_time timestamp,   primary key(guid)   );

## localEnv 
mysql8
java8










