CREATE INDEX idx_users_login_password ON Users(login, password);
CREATE INDEX idx_items_type_price_name ON Items(typeOfItem, price, itemName);
CREATE INDEX idx_foodorder_login_timestamp ON FoodOrder(login, orderTimestamp DESC);
CREATE INDEX idx_orderid_itemname ON ItemsInOrder(orderID, itemName);