-- SET SESSION  sql_mode = 'ANSI';
-- Sense2Web triple store
--
-- Create schema dblist
--
CREATE SCHEMA IF NOT EXISTS db_list;
USE dblist;
--
-- Definition of table 'Nodes'
--
CREATE TABLE IF NOT EXISTS Stores (
  id int unsigned NOT NULL AUTO_INCREMENT,  
  geohash varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (id),
);
