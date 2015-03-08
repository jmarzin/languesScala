# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE themes
    ALTER last_update SET DEFAULT current_timestamp;
ALTER TABLE words
    ALTER last_update SET DEFAULT current_timestamp;
ALTER TABLE verbs
    ALTER last_update SET DEFAULT current_timestamp;
ALTER TABLE forms
    ALTER last_update SET DEFAULT current_timestamp;
COMMIT;

# --- !Downs //#C
BEGIN;
ALTER TABLE themes
    ALTER last_update DROP DEFAULT;
ALTER TABLE words
    ALTER last_update DROP DEFAULT;
ALTER TABLE verbs
    ALTER last_update DROP DEFAULT;
ALTER TABLE forms
    ALTER last_update DROP DEFAULT;
COMMIT;

# #ch05-anorm-schema