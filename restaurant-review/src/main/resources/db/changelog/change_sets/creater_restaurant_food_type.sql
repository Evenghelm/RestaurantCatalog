create table restaurant_food_type
(
    restaurant_id bigint not null constraint food_type_restaurant_fk
        references RESTAURANT (id),
    food_type_id bigint not null constraint restaurant_food_type_fk
        references FOOD_TYPE (id),
    is_main_food_type BOOLEAN not null default false,
    constraint restaurant_food_type_pk primary key (restaurant_id, food_type_id)
);

insert into restaurant_food_type (restaurant_id, food_type_id, is_main_food_type)
select id, food_type_id, true from restaurant where food_type_id is not null;