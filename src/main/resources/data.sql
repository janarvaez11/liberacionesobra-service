INSERT INTO proyectos (id, nombre, descripcion) VALUES
    ('11111111-0000-0000-0000-000000000001', 'Proyecto Alfa', 'Proyecto de prueba principal');

INSERT INTO frentes (id, proyecto_id, nombre, residente_id) VALUES
    ('22222222-0000-0000-0000-000000000001', '11111111-0000-0000-0000-000000000001',
     'Frente Norte', '55555555-0000-0000-0000-000000000001');

INSERT INTO actividades (id, frente_id, nombre, descripcion) VALUES
    ('33333333-0000-0000-0000-000000000001', '22222222-0000-0000-0000-000000000001',
     'Encofrado columnas', 'Encofrado de columnas estructurales nivel 2');
