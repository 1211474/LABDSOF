INSERT INTO organisations (id, name, verified) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Farmácia da Boavista', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'Centro de Dia de Campanhã', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'Transporte Solidário do Porto', TRUE),
    ('44444444-4444-4444-4444-444444444444', 'Associação de Vizinhos da Boavista', FALSE),
    ('55555555-5555-5555-5555-555555555555', 'Farmácia Central', TRUE);

INSERT INTO support_services (
    id,
    organisation_id,
    name,
    type,
    coarse_location,
    operating_hours,
    current_status,
    contact_phone,
    last_updated
) VALUES
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        '11111111-1111-1111-1111-111111111111',
        'Farmácia da Boavista',
        'PHARMACY',
        'Boavista',
        '08:00 - 22:00',
        'OPEN',
        '+351220000001',
        NOW()
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        '55555555-5555-5555-5555-555555555555',
        'Farmácia Central',
        'PHARMACY',
        'Campanhã',
        '09:00 - 19:00',
        'TEMPORARILY_CLOSED',
        '+351220000002',
        NOW()
    ),
    (
        'cccccccc-cccc-cccc-cccc-cccccccccccc',
        '22222222-2222-2222-2222-222222222222',
        'Centro de Dia de Campanhã',
        'DAY_CENTRE',
        'Campanhã',
        '08:30 - 17:30',
        'OPEN',
        '+351220000003',
        NOW()
    ),
    (
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '33333333-3333-3333-3333-333333333333',
        'Transporte Solidário do Porto',
        'ADAPTED_TRANSPORT',
        'Boavista',
        '08:00 - 18:00',
        'OPEN',
        '+351220000004',
        NOW()
    ),
    (
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        '44444444-4444-4444-4444-444444444444',
        'Apoio ao Domicílio dos Vizinhos',
        'HOME_SUPPORT',
        'Boavista',
        '09:00 - 20:00',
        'UNKNOWN',
        '+351220000005',
        NOW()
    );
