-- Insert Vehicle A
INSERT INTO vehicles (vehicle_id, vehicle_name)
SELECT 1, 'Vehicle A'
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles WHERE vehicle_id = 1
);

-- Insert Vehicle B
INSERT INTO vehicles (vehicle_id, vehicle_name)
SELECT 2, 'Vehicle B'
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles WHERE vehicle_id = 2
);

-- Insert Vehicle C
INSERT INTO vehicles (vehicle_id, vehicle_name)
SELECT 3, 'Vehicle C'
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles WHERE vehicle_id = 3
);

-- Insert Vehicle D
INSERT INTO vehicles (vehicle_id, vehicle_name)
SELECT 4, 'Vehicle D'
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles WHERE vehicle_id = 4
);

-- Insert Vehicle E
INSERT INTO vehicles (vehicle_id, vehicle_name)
SELECT 5, 'Vehicle E'
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles WHERE vehicle_id = 5
);

-- Cleaners for Vehicle A (vehicle_id = 1)
INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner A1', 1
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner A1' AND vehicle_id = 1
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner A2', 1
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner A2' AND vehicle_id = 1
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner A3', 1
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner A3' AND vehicle_id = 1
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner A4', 1
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner A4' AND vehicle_id = 1
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner A5', 1
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner A5' AND vehicle_id = 1
);

-- Cleaners for Vehicle B (vehicle_id = 2)
INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner B1', 2
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner B1' AND vehicle_id = 2
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner B2', 2
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner B2' AND vehicle_id = 2
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner B3', 2
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner B3' AND vehicle_id = 2
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner B4', 2
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner B4' AND vehicle_id = 2
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner B5', 2
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner B5' AND vehicle_id = 2
);

-- Cleaners for Vehicle C (vehicle_id = 3)
INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner C1', 3
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner C1' AND vehicle_id = 3
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner C2', 3
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner C2' AND vehicle_id = 3
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner C3', 3
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner C3' AND vehicle_id = 3
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner C4', 3
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner C4' AND vehicle_id = 3
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner C5', 3
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner C5' AND vehicle_id = 3
);

-- Cleaners for Vehicle D (vehicle_id = 4)
INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner D1', 4
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner D1' AND vehicle_id = 4
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner D2', 4
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner D2' AND vehicle_id = 4
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner D3', 4
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner D3' AND vehicle_id = 4
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner D4', 4
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner D4' AND vehicle_id = 4
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner D5', 4
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner D5' AND vehicle_id = 4
);

-- Cleaners for Vehicle E (vehicle_id = 5)
INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner E1', 5
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner E1' AND vehicle_id = 5
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner E2', 5
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner E2' AND vehicle_id = 5
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner E3', 5
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner E3' AND vehicle_id = 5
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner E4', 5
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner E4' AND vehicle_id = 5
);

INSERT INTO cleaners (cleaner_name, vehicle_id)
SELECT 'Cleaner E5', 5
WHERE NOT EXISTS (
    SELECT 1 FROM cleaners WHERE cleaner_name = 'Cleaner E5' AND vehicle_id = 5
);