# CrDrReport
Report viewer

password for db file: shuttle

Production Replication 

	 User table by enabling ddl-auto = create

	role table by 
		create table roles (role_id int not null auto_increment key, role_name varchar(45) not null);
	
	user_roles by
		create table users_roles ( `user_id` bigint not null, `role_id` int not null, key `user_fk_idx` (`user_id`), key `role_fk_idx` (`role_id`), 
		constraint `role_fk` foreign key (`role_id`) references `roles` (`role_id`), 
		constraint `user_fk` foreign key (`user_id`) references `users` (`id`));


# AWS Cost: 
	Amazon Elastic BeanStack
		Amazon EC2 Instances $47
		Amazon S3 Cloud Storage $2
		Amazon RDS Database $2


## Users: 
	Role : 
		prop <admin> : $huttl3
		dev <admin> : Cr3at0r
		users<user-admin> : view

# MS Access Export to MySQL
	open database
	open table
	External Data<tab> -> More -> Export to ODBC Database
	enter desired table name
	Select data Source -> Machine Data Source
	Select MySQL entry -> if doesn't exist
		open control panel -> Administrative tools -> ODBC Data Sources
		Add new user DSN
		Enter SQL server details
	Export complete.