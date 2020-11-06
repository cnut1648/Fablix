CREATE database moviedb;
USE moviedb;


CREATE TABLE IF NOT EXISTS movies
(
    id       VARCHAR(10)  NOT NULL,
    title    TEXT NOT NULL,
    year     INT          NOT NULL,
    director VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FULLTEXT ( title )
);

CREATE TABLE IF NOT EXISTS stars
(
    id        VARCHAR(10)  NOT NULL,
    name      VARCHAR(100) NOT NULL,
    birthYear INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stars_in_movies
(
    starId  VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars (id)
        ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies
(
    genreId  INT         NOT NULL,
    movieId VARCHAR(32) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres (id)
        ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS creditcards
(
    id         VARCHAR(20) NOT NULL,
    firstName  VARCHAR(50) NOT NULL,
    lastName   VARCHAR(50) NOT NULL,
    expiration DATE        NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS customers
(
    id        INT          NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(50)  NOT NULL,
    lastName  VARCHAR(50)  NOT NULL,

    ccId      VARCHAR(20)  NOT NULL,
    address   VARCHAR(200) NOT NULL,
    email     VARCHAR(50)  NOT NULL,
    password  VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ccId) REFERENCES creditcards (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sales
(
    id         INT         NOT NULL AUTO_INCREMENT,
    customerId INT         NOT NULL,
    movieId    VARCHAR(10) NOT NULL,
    saleDate   DATE        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES customers (id)
        ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies (id)
        ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS ratings
(
    movieId  VARCHAR(10) NOT NULL,
    rating   FLOAT       NOT NULL,
    numVotes INT         NOT NULL,
    INDEX (rating),
    FOREIGN KEY (movieId) REFERENCES movies (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS employees
(
    email varchar(50) primary key,
    password varchar(20) not null,
    fullname varchar(100)
);