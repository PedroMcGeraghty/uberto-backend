CREATE OR REPLACE FUNCTION current_year_trips(p_id INT)
    RETURNS SETOF trip
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
        SELECT *
        FROM trip
        WHERE passenger_id = p_id
          AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE);
END;
$$;


SELECT * FROM current_year_trips(5);

DROP Function current_year_trips(p_id INT)