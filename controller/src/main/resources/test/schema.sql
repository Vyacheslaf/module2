create schema if not exists rest_api_db;

drop table if exists gift_certificate_tag;
drop table if exists tag;
drop table if exists gift_certificate;

  CREATE TABLE tag (
  id INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (id));

CREATE TABLE gift_certificate (
  id INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  price INT NOT NULL,
  duration INT NOT NULL,
  create_date TIMESTAMP(3) NOT NULL,
  last_update_date TIMESTAMP(3) NOT NULL,
  PRIMARY KEY (id));

CREATE TABLE gift_certificate_tag (
  gift_certificate_id INT NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY (gift_certificate_id, tag_id),
  CONSTRAINT fk_gift_certificate
    FOREIGN KEY (gift_certificate_id)
    REFERENCES gift_certificate (id) ON DELETE CASCADE
									 ON UPDATE CASCADE,
  CONSTRAINT fk_tag
    FOREIGN KEY (tag_id)
    REFERENCES tag (id) ON DELETE CASCADE
						ON UPDATE CASCADE);


