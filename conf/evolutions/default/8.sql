# #ch05-anorm-schema

# --- !Ups
BEGIN;
CREATE UNIQUE INDEX theme_in_french on words (theme_id, in_french);
COMMIT;


# --- !Downs //#C
BEGIN;
DROP INDEX theme_in_french ;
COMMIT;

# #ch05-anorm-schema