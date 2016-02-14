# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE words
  ADD pronunciation VARCHAR;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE words
  DROP pronunciation ;
COMMIT;

# #ch05-anorm-schema