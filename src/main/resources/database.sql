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

CREATE TABLE movie
(
  	ytId		varchar(11)	NOT NULL,
    status VARCHAR(50) NOT NULL,
    subtitleLocation VARCHAR(500),
    nameOfSavedMovieFile VARCHAR(200),
    unique (ytId)
);