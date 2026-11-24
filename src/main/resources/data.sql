-- Run manually in the same MySQL schema as schema.sql.
-- The sample deadline is tomorrow in Hong Kong local time.
START TRANSACTION;

INSERT INTO programmes (name, application_deadline)
SELECT 'CIS Demo Programme', DATE_ADD(UTC_TIMESTAMP(), INTERVAL 32 HOUR)
WHERE NOT EXISTS (
    SELECT 1 FROM programmes WHERE name = 'CIS Demo Programme'
);

INSERT INTO programme_levels (programme_id, `level`, seat_limit)
SELECT p.id, levels.grade, 30
FROM programmes p
CROSS JOIN (
    SELECT 1 AS grade UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) levels
WHERE p.name = 'CIS Demo Programme'
  AND NOT EXISTS (
      SELECT 1 FROM programme_levels pl
      WHERE pl.programme_id = p.id AND pl.`level` = levels.grade
  );

COMMIT;
