# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE words
  ADD language_level CHAR(1) DEFAULT '1';
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE words
  DROP language_lev ;
COMMIT;

# #ch05-anorm-schema