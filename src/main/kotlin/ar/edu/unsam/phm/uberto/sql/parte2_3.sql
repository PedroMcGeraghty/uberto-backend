
CREATE OR REPLACE FUNCTION passenger_with_N_pendingTrips(number_pending_trips INT)
    RETURNS TABLE(
        passenger_id BIGINT,
        passenger_name TEXT, --CONCAT devuelve TEXT
        passenger_cellphone INTEGER,
        pending_trips_count BIGINT --COunt devuelve BIGINT
    )
    LANGUAGE plpgsql AS
$$
BEGIN

    IF number_pending_trips <= 0 THEN
        RAISE EXCEPTION 'El número de viajes pendientes debe ser mayor que 0. Valor recibido: %', number_pending_trips;
    END IF;

    RETURN QUERY
        SELECT
            p.id AS passenger_id,
            CONCAT(p.first_name, ' ', p.last_name) AS passenger_name,
            p.cellphone AS passenger_cellphone,
            COUNT(t.id) AS pending_trips_count
        FROM
            passenger p
        JOIN
            trip t ON p.id = t.passenger_id
        WHERE
            (t.date + (t.duration * INTERVAL '1 minute')) > NOW()
        GROUP BY
            p.id
        HAVING
            COUNT(t.id) > number_pending_trips
        ORDER BY
            pending_trips_count DESC;
END;
$$;

-- LLAMADA A LA FUNCION
SELECT * FROM passenger_with_N_pendingTrips(1)
