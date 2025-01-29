insert into measurements (id, sensor_id, reading, time)
values (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 1000, CURRENT_TIMESTAMP - INTERVAL '1 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 1450, CURRENT_TIMESTAMP - INTERVAL '2 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 1560, CURRENT_TIMESTAMP - INTERVAL '5 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 1990, CURRENT_TIMESTAMP - INTERVAL '16 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 2003, CURRENT_TIMESTAMP - INTERVAL '29 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 1985, CURRENT_TIMESTAMP - INTERVAL '31 days'),
       (gen_random_uuid(), 'c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103', 2020, CURRENT_TIMESTAMP - INTERVAL '32 days');
