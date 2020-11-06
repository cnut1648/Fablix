DROP PROCEDURE IF EXISTS add_movie;

DELIMITER //
CREATE PROCEDURE add_movie
(IN title varchar(100),
IN year int(11),
IN director varchar(100),
IN star_name VARCHAR(50),
IN genre VARCHAR(200),
OUT responseMsg VARCHAR(200)
)
BEGIN
	DECLARE already_have INT DEFAULT 0;
    DECLARE movie_already_have INT DEFAULT 0;
	DECLARE P_MovieID INT DEFAULT 0;
    DECLARE P_sid INT DEFAULT 0;
    DECLARE MovieID varchar(100) DEFAULT Null ;
    DECLARE tid varchar(100) DEFAULT null ;
    DECLARE stid varchar(100) DEFAULT null ;
	DECLARE star_id varchar(100) DEFAULT null;
	DECLARE genre_id varchar(100) DEFAULT null ;
	
    SELECT movies.id, count(*)
	INTO MovieID, movie_already_have
	FROM movies 
	WHERE  movies.title = title AND movies.year = year AND movies.director = director
    group by id;

	proc_label:BEGIN
		IF movie_already_have > 0 THEN
			SET @responseMsg = 'Movie not added, already exists.';
		ELSE
			SET @responseMsg = 'Movie added. ';
            SELECT MAX(CAST(SUBSTRING(id, 3, length(id)-2) AS UNSIGNED))+1 
            into P_MovieID 
            FROM movies;
            SET tid=Cast(P_MovieID as char(100));
            SET MovieID = concat('tt0' , tid);
			INSERT INTO movies (id,title, year, director) 
			VALUES (MovieID,title, year, director);
            SET @responseMsg = concat(@responseMsg , 'With Movie Id: ') ;
            SET @responseMsg = concat(@responseMsg , MovieID) ;
			
		END IF;

		SELECT id, count(*) 
		INTO star_id, already_have
		FROM stars 
		WHERE stars.name=star_name
        group by id;

		IF already_have = 0  And star_name != "" And movie_already_have = 0 THEN
			SELECT MAX(CAST(SUBSTRING(id, 3, length(id)-2) AS UNSIGNED))+1 
            into P_sid 
            FROM stars;
            SET stid=Cast(P_sid as char(100));
            SET star_id = concat('nm' , stid);
			INSERT INTO stars(id,name,birthYear) values (star_id,star_name,null);
			
		END IF;
        
        IF genre != "" THEN
			SELECT id, count(*) 
			INTO genre_id, already_have 
			FROM genres 
			WHERE genres.name = genre
            group by id;
		END IF;

		IF already_have = 0  AND genre != "" And movie_already_have = 0 THEN
            SELECT MAX(id)+1 
            into genre_id
            FROM genres
            ;
			INSERT INTO genres (id,name) VALUES (genre_id,genre);
			
		END IF;
        
        if  movie_already_have = 0 then
		INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, MovieID);
        SET @responseMsg = concat(@responseMsg , ', With Genre Id: ') ;
		SET @responseMsg = concat(@responseMsg , genre_id) ;
        INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, MovieID);
        SET @responseMsg = concat(@responseMsg , ', With Star Id: ') ;
		SET @responseMsg = concat(@responseMsg , star_id) ;
        end if;
        
       
	END;
    SET responseMsg = @responseMsg;
END //
DELIMITER ;


DROP PROCEDURE IF EXISTS add_star;

DELIMITER //
CREATE PROCEDURE add_star
(
IN star_name VARCHAR(50),
IN star_dob int(11),
OUT responseMsg VARCHAR(200)
)
BEGIN
	DECLARE already_have INT DEFAULT 0;
	
    DECLARE P_sid INT DEFAULT 0;
    DECLARE tid varchar(100) DEFAULT null ;
    DECLARE stid varchar(100) DEFAULT null ;
	DECLARE star_id varchar(100) DEFAULT null;
	proc_label:BEGIN
		SELECT id, count(*) 
		INTO star_id, already_have
		FROM stars 
		WHERE stars.name=star_name
        group by id;

		IF already_have = 0  And star_name != ""THEN
			SELECT MAX(CAST(SUBSTRING(id, 3, length(id)-2) AS UNSIGNED))+1 
            into P_sid 
            FROM stars;
            SET stid=Cast(P_sid as char(100));
            SET star_id = concat('nm' , stid);
			INSERT INTO stars(id,name,birthYear) values (star_id,star_name,star_dob);
			SET @responseMsg =('Star was not found and was created. With StarId: ') ;
            SET @responseMsg =concat(@responseMsg,star_id) ;
		END IF;
	END;
    SET responseMsg = @responseMsg;
END //
DELIMITER ;