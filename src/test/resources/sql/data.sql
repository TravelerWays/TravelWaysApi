insert into app_user (id, create_at, update_at, active, email, hash, name, password, surname, username, image_id)
values  (1, '2022-12-07 12:52:25.545000', '2022-12-07 12:52:25.583000', true, 'test@example.com', '44f759f6-0f9c-46f1-8b81-c07f0cecfed1', 'John_1', '$2a$10$O35/NqoR9N0H0C2j4AvLMue..8h4640Naa9Il6MsvN34QaIq9puOe', 'Doe_1', 'JD_1', null),
        (2, '2022-12-07 12:52:25.545000', '2022-12-07 12:52:25.583000', true, 'test2@example.com', '44f759f6-0f9c-46f1-8b81-c07f0cecfed1', 'John_2', '$2a$10$O35/NqoR9N0H0C2j4AvLMue..8h4640Naa9Il6MsvN34QaIq9puOe', 'Doe_2', 'JD_2', null);

insert into public.role (id, create_at, update_at, name)
values  (1, '2022-12-07 12:52:25.219000', '2022-12-07 12:52:25.219000', 'ROLE_USER');

insert into public.app_user_roles (app_user_id, roles_id)
values  (1, 1),
        (2, 1);

