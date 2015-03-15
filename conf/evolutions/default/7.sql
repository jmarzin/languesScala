# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE forms
  ALTER language_id TYPE CHAR (2);
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE forms
  ALTER language_id TYPE INTEGER ;
COMMIT;

# #ch05-anorm-schema