UPDATE driver
    SET base_price = 0.0
    WHERE base_price IS NULL;

    ALTER TABLE driver
    ALTER COLUMN base_price SET NOT NULL;
