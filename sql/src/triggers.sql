CREATE OR REPLACE FUNCTION log_status_change()
RETURNS "trigger" AS
$BODY$
BEGIN
    -- Only update timestamp if the status changed
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
    SELECT COALESCE(SUM(i.price * io.quantity), 0.00)
    INTO calculated_total
    FROM ItemsInOrder io
    JOIN Items i ON io.itemName = i.itemName
    WHERE io.orderID = NEW.orderID;
    
    UPDATE FoodOrder
    SET totalPrice = calculated_total
    WHERE orderID = NEW.orderID;
    
    RETURN NULL; -- for AFTER triggers
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS calculate_order_total ON ItemsInOrder;
CREATE TRIGGER calculate_order_total
AFTER INSERT OR UPDATE OR DELETE ON ItemsInOrder
FOR EACH ROW
EXECUTE PROCEDURE update_order_total();