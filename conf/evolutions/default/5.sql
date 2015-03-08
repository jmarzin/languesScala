# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE words
  DROP COLUMN IF EXISTS pronunciation;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE words
  ADD pronunciation VARCHAR;
COMMIT;

# #ch05-anorm-schema