# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE themes
  ADD supp CHAR(1) DEFAULT 'f' ;
ALTER TABLE words
  ADD supp CHAR(1) DEFAULT 'f' ;
ALTER TABLE verbs
  ADD supp CHAR(1) DEFAULT 'f' ;
ALTER TABLE forms
  ADD supp CHAR(1) DEFAULT 'f' ;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE themes
  DROP COLUMN IF EXISTS supp ;
ALTER TABLE words
  DROP COLUMN IF EXISTS supp ;
ALTER TABLE verbs
  DROP COLUMN IF EXISTS supp ;
ALTER TABLE forms
  DROP COLUMN IF EXISTS supp  ;
COMMIT;

# #ch05-anorm-schema