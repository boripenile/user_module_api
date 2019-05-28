CREATE TABLE `images` (
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`image_blob` LONGBLOB,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `organisations`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`code` VARCHAR(50) NOT NULL,
	`referral_code` VARCHAR(50),
	`parent_code` VARCHAR(50),
	`name` VARCHAR(50) NOT NULL,
	`description` VARCHAR(150),
	`motto` VARCHAR(100),
	`image_url` VARCHAR(150),
	`image_id` BIGINT(11),
	`active` TINYINT(1) DEFAULT '1',
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`version` INT(11) DEFAULT '0',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `applications`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`app_code` VARCHAR(20) NOT NULL,
	`app_name` VARCHAR(50) NOT NULL,
	`app_description` VARCHAR(100),
	`app_version` VARCHAR(20),
	`image_url` VARCHAR(150),
	`image_id` BIGINT(11),
	`organisation_id` BIGINT(11) NOT NULL,
	`active` TINYINT(1) DEFAULT '0',
	`latest` TINYINT(1) DEFAULT '0',
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`version` INT(11) DEFAULT '0',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

--
--CREATE TABLE `applications_organisations`(
--	`id` BIGINT(11) auto_increment PRIMARY KEY,
--	`application_id` BIGINT(11) NOT NULL,
--	`organisation_id` BIGINT(11) NOT NULL,
--	`created_at` DATETIME DEFAULT NULL,
--	`updated_at` DATETIME DEFAULT NULL,
--	`active` TINYINT(1) DEFAULT '1',
--	`created_by` VARCHAR(50) NOT NULL,
--	`updated_by` VARCHAR(50)
--)ENGINE=InnoDB;

CREATE TABLE `addresses` (
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`phone_number` VARCHAR(50) NOT NULL,
	`address` VARCHAR(200) DEFAULT NULL,
	`address2` VARCHAR(200) DEFAULT NULL,
	`city` VARCHAR(50) DEFAULT NULL,
	`state` VARCHAR(50) DEFAULT NULL,
	`country` VARCHAR(50) DEFAULT NULL,
	`location` LONGBLOB DEFAULT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `users` (
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`address_id` BIGINT(11) NOT NULL,
	`username` VARCHAR(50) NOT NULL,
	`password` VARCHAR(248) NOT NULL,
	`email_address` VARCHAR(50),
	`first_name` VARCHAR(50),
	`last_name` VARCHAR(50),
	`other_name` VARCHAR(50),
	`email_verified` TINYINT(1) DEFAULT '0',
	`phone_verified` TINYINT(1) DEFAULT '0',
	`email_verified_date` DATETIME,
	`phone_verified_date` DATETIME,
	`verification_code` VARCHAR(10) DEFAULT NULL,
	`reset_password_code` VARCHAR(10) DEFAULT NULL,
	`reset_password_request_date` DATETIME DEFAULT NULL, 
	`reset_password_date` DATETIME DEFAULT NULL,
	`verification_expired_date` DATETIME DEFAULT NULL, 
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`deleted` TINYINT(1) DEFAULT '0',
	`version` INT(11) DEFAULT '0',
	`active` TINYINT(1) DEFAULT '0',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `users_organisations` (
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`user_id` BIGINT(11) NOT NULL,
	`organisation_id` BIGINT(11) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `users_applications` (
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`user_id` BIGINT(11) NOT NULL,
	`application_id` BIGINT(11) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `roles`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`application_id` BIGINT(11) NOT NULL,
	`role_name` VARCHAR(50) NOT NULL,
	`description` VARCHAR(200) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `permissions`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`application_id` BIGINT(11) NOT NULL,
	`permission_name` VARCHAR(50) NOT NULL,
	`description` VARCHAR(200) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB; 

CREATE TABLE `users_roles`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`user_id` BIGINT(11) NOT NULL,
	`role_id` BIGINT(11) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

CREATE TABLE `roles_permissions`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`role_id` BIGINT(11) NOT NULL,
	`permission_id` BIGINT(11) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB; 

CREATE TABLE `users_permissions`(
	`id` BIGINT(11) auto_increment PRIMARY KEY,
	`user_id` BIGINT(11) NOT NULL,
	`permission_id` BIGINT(11) NOT NULL,
	`created_at` DATETIME DEFAULT NULL,
	`updated_at` DATETIME DEFAULT NULL,
	`active` TINYINT(1) DEFAULT '1',
	`created_by` VARCHAR(50) NOT NULL,
	`updated_by` VARCHAR(50)
)ENGINE=InnoDB;

ALTER TABLE users
ADD CONSTRAINT FK_users_addresses
FOREIGN KEY (address_id) REFERENCES addresses(id);

ALTER TABLE organisations
ADD CONSTRAINT FK_organisations_images
FOREIGN KEY (image_id) REFERENCES images(id);

ALTER TABLE applications
ADD CONSTRAINT FK_applications_images
FOREIGN KEY (image_id) REFERENCES images(id);

ALTER TABLE applications
ADD CONSTRAINT FK_applications_organisations
FOREIGN KEY (organisation_id) REFERENCES organisations(id);

ALTER TABLE users_organisations
ADD CONSTRAINT FK_users_organisations
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE users_organisations
ADD CONSTRAINT FK_organisations_users
FOREIGN KEY (organisation_id) REFERENCES organisations(id);

ALTER TABLE users_applications
ADD CONSTRAINT FK_users_applications
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE users_applications
ADD CONSTRAINT FK_applications_users
FOREIGN KEY (application_id) REFERENCES applications(id);

ALTER TABLE roles
ADD CONSTRAINT FK_roles_applications
FOREIGN KEY (application_id) REFERENCES applications(id);

ALTER TABLE permissions
ADD CONSTRAINT FK_permissions_applications
FOREIGN KEY (application_id) REFERENCES applications(id);

ALTER TABLE roles_permissions
ADD CONSTRAINT FK_roles_permissions
FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE roles_permissions
ADD CONSTRAINT FK_permissions_roles
FOREIGN KEY (permission_id) REFERENCES permissions(id);

ALTER TABLE users_permissions
ADD CONSTRAINT FK_users_permissions
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE users_permissions
ADD CONSTRAINT FK_permissions_users
FOREIGN KEY (permission_id) REFERENCES permissions(id);

ALTER TABLE users_roles
ADD CONSTRAINT FK_users_roles
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE users_roles
ADD CONSTRAINT FK_roles_users
FOREIGN KEY (role_id) REFERENCES roles(id);

INSERT INTO `organisations` (`id`, `code`, `name`, `description`, `created_at`, `created_by`)
VALUES (1, '123456', 'Youngprime Solution Ventures', 'Simplifying processes with software', NOW(), 'system');

INSERT INTO `addresses` (`id`, `phone_number`, `created_at`, `created_by`)
VALUES (1, '08069566914', NOW(), 'admin');

INSERT INTO `users` (`id`, `address_id`, `username`, `password`, `created_at`, `created_by`, `active`)
VALUES (1, '1', 'admin', '746a5a2664633cb15829e80cc8d5dd7368b1d939756e7b069df9df482e2afc3c44029ec71ffbf7cc9916719d861b60fc34b5bd6a4f2cb0fe7747d99d5b219162', NOW(), 'system', 1);

INSERT INTO `users_organisations` (`id`, `user_id`, `organisation_id`, `created_at`, `created_by`)
VALUES (1, '1', '1', NOW(), 'admin');

INSERT INTO `applications` (`id`, `app_code`, `app_name`, `app_description`, `app_version`, `organisation_id`, `active`, `created_at`, `created_by`)
VALUES (1, 'CJK2233448', 'Car dealers System', 'Managing of car business.', '1.0.0', 1, 1, NOW(), 'system');

INSERT INTO `roles` (`id`, `application_id`, `role_name`, `description`, `created_at`, `created_by`) 
VALUES (1, 1, 'superadmin', 'Super administrator', NOW(), 'system');

INSERT INTO `roles` (`id`, `application_id`,`role_name`, `description`, `created_at`, `created_by`) 
VALUES (2, 1, 'admin', 'Administrator', NOW(), 'system');

INSERT INTO `roles` (`id`, `application_id`,`role_name`, `description`, `created_at`, `created_by`) 
VALUES (3, 1, 'client', 'Client', NOW(), 'system');

INSERT INTO `roles` (`id`, `application_id`,`role_name`, `description`, `created_at`, `created_by`) 
VALUES (4, 1, 'staff', 'Staff', NOW(), 'system');

INSERT INTO `roles` (`id`, `application_id`,`role_name`, `description`, `created_at`, `created_by`) 
VALUES (5, 1, 'customer', 'Customer', NOW(), 'system');

INSERT INTO `permissions` (`id`, `application_id`,`permission_name`, `description`, `created_at`, `created_by`) 
VALUES (1, 1, '*', 'All', NOW(), 'system');

INSERT INTO `permissions` (`id`, `application_id`,`permission_name`, `description`, `created_at`, `created_by`) 
VALUES (2, 1, 'roles:list', 'View all roles', NOW(), 'system');

INSERT INTO `permissions` (`id`, `application_id`,`permission_name`, `description`, `created_at`, `created_by`) 
VALUES (3, 1, 'roles:view', 'View a role', NOW(), 'system');

INSERT INTO `users_roles` (`id`, `user_id`, `role_id`, `created_at`, `created_by`)
VALUES (1, '1', '1', NOW(), 'system');

INSERT INTO `users_applications` (`id`, `user_id`, `application_id`, `created_at`, `created_by`)
VALUES (1, '1', '1', NOW(), 'system');

INSERT INTO `roles_permissions` (`id`, `role_id`, `permission_id`, `created_at`, `created_by`)
VALUES (1, '1', '1', NOW(), 'system');

INSERT INTO `roles_permissions` (`id`, `role_id`, `permission_id`, `created_at`, `created_by`)
VALUES (2, '1', '2', NOW(), 'system');

INSERT INTO `roles_permissions` (`id`, `role_id`, `permission_id`, `created_at`, `created_by`)
VALUES (3, '1', '3', NOW(), 'system');


