-- SET SESSION  sql_mode = 'ANSI';
-- Sense2Web triple store
--
-- Create schema resourcedb
--
CREATE SCHEMA IF NOT EXISTS triplestore;
-- USE resourcedb;
SET SCHEMA triplestore;
--
-- Definition of table 'Nodes'
--
CREATE TABLE IF NOT EXISTS Nodes (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  "HASH" bigint(20) NOT NULL DEFAULT '0',
  lex longtext,
  lang varchar(10) NOT NULL DEFAULT '',
  datatype varchar(200) NOT NULL DEFAULT '',
  "TYPE" int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY "Hash" ("HASH")
);

--
-- Definition of table 'Prefixes'
--
CREATE TABLE IF NOT EXISTS Prefixes (
  "PREFIX" varchar(50) NOT NULL,
  uri varchar(500) NOT NULL,
  PRIMARY KEY ("PREFIX")
);
--
-- Definition of table 'Quads'
--
CREATE TABLE IF NOT EXISTS Quads (
  g int(11) NOT NULL,
  s int(11) NOT NULL,
  p int(11) NOT NULL,
  o int(11) NOT NULL,
  PRIMARY KEY (g,s,p,o)
);
--
-- Definition of table 'Triples'
--
CREATE TABLE IF NOT EXISTS Triples (
  s int(11) NOT NULL,
  p int(11) NOT NULL,
  o int(11) NOT NULL,
  PRIMARY KEY (s,p,o)
);