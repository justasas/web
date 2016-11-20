CREATE TABLE movie
(
	id		SERIAL		NOT NULL,
	title		varchar(50)	NOT NULL,
	rating		decimal(10,2) NOT NULL,
	year		int		NOT NULL,
	duration	int		NOT NULL,
	ytId		varchar(11)	NOT NULL,
	PRIMARY KEY (id),
	unique (title)
);
