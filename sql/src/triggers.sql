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

DROP TRIGGER IF EXISTS item_quantity_validation ON ItemsInOrder;
CREATE TRIGGER item_quantity_validation
BEFORE INSERT OR UPDATE ON ItemsInOrder
FOR EACH ROW
EXECUTE PROCEDURE validate_item_quantity();


CREATE OR REPLACE FUNCTION log_status_change()
RETURNS "trigger" AS
$BODY$
BEGIN
    -- Only update timestamp if status has changed
    IF OLD.orderStatus <> NEW.orderStatus THEN
        NEW.orderTimestamp = CURRENT_TIMESTAMP;
        
    END IF;
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;


DROP TRIGGER IF EXISTS order_status_update_trigger ON FoodOrder;
CREATE TRIGGER order_status_update_trigger
BEFORE UPDATE ON FoodOrder
FOR EACH ROW
WHEN (OLD.orderStatus IS DISTINCT FROM NEW.orderStatus)
EXECUTE PROCEDURE log_status_change();


CREATE OR REPLACE FUNCTION update_order_total()
RETURNS "trigger" AS
$BODY$
DECLARE
    calculated_total DECIMAL(10,2);
BEGIN
    -- Calculate the new total price based on items and quantities
    SELECT COALESCE(SUM(i.price * io.quantity), 0.00)
    INTO calculated_total
    FROM ItemsInOrder io
    JOIN Items i ON io.itemName = i.itemName
    WHERE io.orderID = NEW.orderID;
    
    -- Update the order with the calculated total
    UPDATE FoodOrder
    SET totalPrice = calculated_total
    WHERE orderID = NEW.orderID;
    
    RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS calculate_order_total ON ItemsInOrder;
CREATE TRIGGER calculate_order_total
AFTER INSERT OR UPDATE OR DELETE ON ItemsInOrder
FOR EACH ROW
EXECUTE PROCEDURE update_order_total();
