-- enum type for payment-type
CREATE TYPE payment_method as ENUM (
	'cash',
	'cash_on_delivery',
	'visa',
	'mastercard',
	'amex',
	'jcb'
);

-- create table payment methods
CREATE TABLE IF NOT EXISTS payment_methods(
	id						SERIAL				NOT NULL,
	payment_method			payment_method		NOT NULL,
	price_modifier_lower	FLOAT				NOT NULL,
	price_modifier_upper	FLOAT				NOT NULL,
	points_applicable		FLOAT				NOT NULL,
	-- constraints:
	CONSTRAINT payment_methods_pk PRIMARY KEY(id)
);

-- create table sales
CREATE TABLE IF NOT EXISTS sales(
	id						SERIAL						NOT NULL,
	amount					FLOAT						NOT NULL,
	sale_datetime			TIMESTAMP WITH TIME ZONE	NOT NULL		DEFAULT now(),
	points_provided			INTEGER						NOT NULL,
	-- constraints
	CONSTRAINT sales_pk PRIMARY KEY(id)
);

-- truncate table payment methods to ensure values are not inserted twice
TRUNCATE TABLE payment_methods;

-- insert defaults into payment_methods
INSERT INTO payment_methods 
	(payment_method, price_modifier_lower, price_modifier_upper, points_applicable)
VALUES
	('cash', 				0.90, 1.00, 0.05),
	('cash_on_delivery', 	1.00, 1.02, 0.05),
	('visa', 				0.95, 1.00, 0.03),
	('mastercard', 			0.95, 1.00, 0.03),
	('amex', 				0.98, 1.01, 0.02),
	('jcb', 				0.95, 1.00, 0.05)
;