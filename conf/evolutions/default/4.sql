# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE themes
    ALTER last_update TYPE VARCHAR,
    ALTER last_update DROP DEFAULT;
ALTER TABLE words
    ALTER last_update TYPE VARCHAR;
ALTER TABLE verbs
    ALTER last_update TYPE VARCHAR;
ALTER TABLE forms
    ALTER last_update TYPE VARCHAR;
COMMIT;

# --- !Downs //#C
BEGIN;
ALTER TABLE themes
    ALTER last_update TYPE TIMESTAMP,
    ALTER last_update SET DEFAULT current_timestamp;
ALTER TABLE words
    ALTER last_update TYPE TIMESTAMP;
ALTER TABLE verbs
    ALTER last_update TYPE TIMESTAMP;
ALTER TABLE forms
    ALTER last_update TYPE TIMESTAMP;
COMMIT;

# #ch05-anorm-schema