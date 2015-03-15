# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE forms
  DROP COLUMN IF EXISTS pronuncation;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE forms
  ADD pronuncation VARCHAR;
COMMIT;

# #ch05-anorm-schema