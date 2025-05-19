package ar.edu.unsam.phm.uberto.sql


DROP TABLE IF EXISTS balance_history CASCADE;
CREATE TABLE IF NOT EXISTS balance_history (
    id SERIAL PRIMARY KEY,
    passenger_id INTEGER REFERENCES passenger(id),
    modification_date TIMESTAMP DEFAULT now(),
    old_balance NUMERIC,
    new_balance NUMERIC
    );


DROP FUNCTION IF EXISTS record_balance_change();
CREATE FUNCTION record_balance_change()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.balance IS DISTINCT FROM OLD.balance THEN
        INSERT INTO balance_history (passenger_id, old_balance, new_balance)
        VALUES (OLD.id, OLD.balance, NEW.balance);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS trigger_balance_change ON passenger;
CREATE TRIGGER trigger_balance_change
    BEFORE UPDATE ON passenger
    FOR EACH ROW
    EXECUTE FUNCTION record_balance_change();
