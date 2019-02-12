create table users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username varchar(32),
    password varchar(40),
    nom varchar(32),
    prenom varchar(32),
    mail varchar(40),
    sexe ENUM('M', 'F')
    );

create table connexion (
	id INT,
	cle VARCHAR(64),
	last_used TIMESTAMP
);

CREATE TABLE friends (userid INT, followsid INT, since DATE);

