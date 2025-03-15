-- Function to validate phone number format
CREATE OR REPLACE FUNCTION validate_phone_number()
RETURNS "trigger" AS
$BODY$
BEGIN
    -- Check if phone number contains only digits
    IF NEW.phoneNum !~ '^[0-9]+$' THEN
        RAISE EXCEPTION 'Phone number must contain only numeric digits';
    END IF;
    
    -- Check phone number length
    IF length(NEW.phoneNum) < 10 THEN
        RAISE EXCEPTION 'Phone number must be at least 10 digits long';
    END IF;
    
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

-- Create the trigger
DROP TRIGGER IF EXISTS phone_number_validation ON Users;
CREATE TRIGGER phone_number_validation
BEFORE INSERT OR UPDATE ON Users
FOR EACH ROW
EXECUTE PROCEDURE validate_phone_number();


-- This trigger ensures that order quantities are valid (greater than zero)
CREATE OR REPLACE FUNCTION validate_item_quantity()
RETURNS "trigger" AS
$BODY$
BEGIN
    IF NEW.quantity <= 0 THEN
        RAISE EXCEPTION 'Item quantity must be greater than zero';
    END IF;
    
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

-- Create the trigger
DROP TRIGGER IF EXISTS item_quantity_validation ON ItemsInOrder;
CREATE TRIGGER item_quantity_validation
BEFORE INSERT OR UPDATE ON ItemsInOrder
FOR EACH ROW
EXECUTE PROCEDURE validate_item_quantity();