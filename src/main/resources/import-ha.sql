CREATE TABLE IF NOT EXISTS vets (
  id SERIAL,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  CONSTRAINT pk_vets PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_vets_last_name ON vets (last_name);

CREATE TABLE IF NOT EXISTS specialties (
  id SERIAL,
  name VARCHAR(80),
  CONSTRAINT pk_specialties PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_specialties_name ON specialties (name);

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id INT NOT NULL,
  specialty_id INT NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (specialty_id) REFERENCES specialties(id),
  CONSTRAINT unique_ids UNIQUE (vet_id,specialty_id)
);



CREATE TABLE IF NOT EXISTS types (
  id SERIAL,
  name VARCHAR(80),
  CONSTRAINT pk_types PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_types_name ON types (name);

CREATE TABLE IF NOT EXISTS owners (
  id SERIAL,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  address VARCHAR(255),
  city VARCHAR(80),
  telephone VARCHAR(20),
  CONSTRAINT pk_owners PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_owners_last_name ON owners (last_name);

CREATE TABLE IF NOT EXISTS pets (
  id SERIAL,
  name VARCHAR(30),
  birth_date DATE,
  type_id INT NOT NULL,
  owner_id INT NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES owners(id),
  FOREIGN KEY (type_id) REFERENCES types(id),
  CONSTRAINT pk_pets PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_pets_name ON pets (name);

CREATE TABLE IF NOT EXISTS visits (
  id SERIAL,
  pet_id INT NOT NULL,
  visit_date DATE,
  description VARCHAR(255),
  FOREIGN KEY (pet_id) REFERENCES pets(id),
  CONSTRAINT pk_visits PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(20) NOT NULL ,
  password VARCHAR(20) NOT NULL ,
  enabled boolean NOT NULL DEFAULT true ,
  CONSTRAINT pk_users PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS roles (
  id SERIAL,
  username varchar(20) NOT NULL,
  role varchar(20) NOT NULL,
  CONSTRAINT pk_roles PRIMARY KEY (id),
  FOREIGN KEY (username) REFERENCES users (username)
);

ALTER TABLE roles ADD CONSTRAINT uni_username_role UNIQUE (role,username);

INSERT INTO vets VALUES (1, 'James', 'Carter') ;
INSERT INTO vets VALUES (2, 'Helen', 'Leary') ;
INSERT INTO vets VALUES (3, 'Linda', 'Douglas') ;
INSERT INTO vets VALUES (4, 'Rafael', 'Ortega') ;
INSERT INTO vets VALUES (5, 'Henry', 'Stevens') ;
INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins') ;

INSERT INTO specialties VALUES (1, 'radiology') ;
INSERT INTO specialties VALUES (2, 'surgery') ;
INSERT INTO specialties VALUES (3, 'dentistry') ;

INSERT INTO vet_specialties VALUES (2, 1) ;
INSERT INTO vet_specialties VALUES (3, 2) ;
INSERT INTO vet_specialties VALUES (3, 3) ;
INSERT INTO vet_specialties VALUES (4, 2) ;
INSERT INTO vet_specialties VALUES (5, 1) ;

INSERT INTO types VALUES (1, 'cat') ;
INSERT INTO types VALUES (2, 'dog') ;
INSERT INTO types VALUES (3, 'lizard') ;
INSERT INTO types VALUES (4, 'snake') ;
INSERT INTO types VALUES (5, 'bird') ;
INSERT INTO types VALUES (6, 'hamster') ;

INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023') ;
INSERT INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749') ;
INSERT INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763') ;
INSERT INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198') ;
INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765') ;
INSERT INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654') ;
INSERT INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387') ;
INSERT INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683') ;
INSERT INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435') ;
INSERT INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487') ;

INSERT INTO pets VALUES (1, 'Leo', '2000-09-07', 1, 1) ;
INSERT INTO pets VALUES (2, 'Basil', '2002-08-06', 6, 2) ;
INSERT INTO pets VALUES (3, 'Rosy', '2001-04-17', 2, 3) ;
INSERT INTO pets VALUES (4, 'Jewel', '2000-03-07', 2, 3) ;
INSERT INTO pets VALUES (5, 'Iggy', '2000-11-30', 3, 4) ;
INSERT INTO pets VALUES (6, 'George', '2000-01-20', 4, 5) ;
INSERT INTO pets VALUES (7, 'Samantha', '1995-09-04', 1, 6) ;
INSERT INTO pets VALUES (8, 'Max', '1995-09-04', 1, 6) ;

-- INSERT INTO pets VALUES (9, 'Lucky', '1999-08-06', 5, 7) ;
-- INSERT INTO pets VALUES (10, 'Mulligan', '1997-02-24', 2, 8) ;
-- INSERT INTO pets VALUES (11, 'Freddy', '2000-03-09', 5, 9) ;
-- INSERT INTO pets VALUES (12, 'Lucky', '2000-06-24', 2, 10) ;
-- INSERT INTO pets VALUES (13, 'Sly', '2002-06-08', 1, 10) ;

INSERT INTO visits (id, pet_id, visit_date, description) VALUES  (1, 7, '2010-03-04', 'rabies shot') ;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (2, 8, '2011-03-04', 'rabies shot') ;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (3, 8, '2009-06-04', 'neutered') ;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (4, 7, '2008-09-04', 'spayed') ;

INSERT INTO users(username,password,enabled) VALUES ('admin','admin', true) ;

INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_OWNER_ADMIN') ;
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_VET_ADMIN') ;
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_ADMIN') ;
