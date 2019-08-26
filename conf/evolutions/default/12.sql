# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE formsTypes
  ADD supp CHAR(1) DEFAULT 'f' ;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE formsTypes
  DROP COLUMN IF EXISTS supp ;
COMMIT;

# #ch05-anorm-schema