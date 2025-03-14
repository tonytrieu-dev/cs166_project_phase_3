-- Creates a trigger that logs the status changes of a FoodOrder
CREATE TABLE OrderStatusHistory (
    id SERIAL PRIMARY KEY,
    orderID INTEGER NOT NULL,
    oldStatus VARCHAR(50),
    newStatus VARCHAR(50),
    changeTime TIMESTAMP DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION log_status_change()
RETURNS "trigger" AS
$BODY$
BEGIN
    INSERT INTO OrderStatusHistory(orderID, oldStatus, newStatus)
    VALUES(NEW.orderID, OLD.status, NEW.status);
    RETURN NEW;
END;
$BODY$

CREATE TRIGGER status_change_trigger
AFTER UPDATE OF orderSTATUS ON FoodOrder
FOR EACH ROW
EXECUTE PROCEDURE log_status_change();