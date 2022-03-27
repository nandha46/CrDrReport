INSERT INTO users (id,email,password,first_name,last_name,enabled) VALUES ('1','nandha@mail.com','$2a$10$E.nXRSPx6vadp9wOrQwMLOOettOskYpORmoTA6e2pmwzqUdt2F6Fu','nandha','Subramanian',1);
INSERT INTO roles (role_id,role_name) values ('1','developer');
INSERT INTO users_roles (user_id,role_id) values ('1','1');