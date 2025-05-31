package ar.edu.unsam.phm.uberto.sql


CREATE OR REPLACE VIEW choferes_con_mas_de_2_viajes_finalizados AS
    SELECT d.id AS driver_id, d.first_name, d.last_name, COUNT(t.id) AS cantidad_viajes_finalizados
    FROM driver d
    JOIN trip t ON d.id = t.driver_id
    WHERE (t.date + INTERVAL '1 minute' * t.duration) < NOW()
    GROUP BY d.id, d.first_name, d.last_name
    HAVING COUNT(t.id) > 2;
