#!/bin/bash
mysql -u username -p password -e "SELECT * FROM Orders WHERE DATE(order_date) = CURDATE();" > daily_report.csv