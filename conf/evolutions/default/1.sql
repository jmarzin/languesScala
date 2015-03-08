# #ch05-anorm-schema

# --- !Ups
BEGIN;
CREATE TABLE themes (
  _id SERIAL PRIMARY KEY,
  languageId char(2),
  number integer,
  inLanguage varchar,
  lastUpdate timestamp);

CREATE TABLE words (
  _id SERIAL PRIMARY KEY ,
  languageId CHAR(2),
  themeId integer REFERENCES themes,
  inFrench varchar,
  sortWord varchar,
  inLanguage varchar,
  pronunciation varchar,
  lastUpdate timestamp);

CREATE TABLE verbs (
  _id SERIAL PRIMARY KEY ,
  language_id char(2),
  inLanguage varchar,
  lastUpdate timestamp);

CREATE TABLE formsTypes (
  _id SERIAL PRIMARY KEY );

CREATE TABLE forms (
  _id SERIAL PRIMARY KEY ,
  verbId integer REFERENCES verbs,
  formTypeId integer REFERENCES formsTypes,
  languageId integer,
  inLanguage varchar,
  pronuncation varchar,
  lastUpdate timestamp);
COMMIT;

# --- !Downs //#C
BEGIN;
DROP TABLE IF EXISTS words;
DROP TABLE IF EXISTS themes;
DROP TABLE IF EXISTS forms;
DROP TABLE IF EXISTS formsTypes;
DROP TABLE IF EXISTS verbs;
COMMIT;

# #ch05-anorm-schema