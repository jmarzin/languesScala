# #ch05-anorm-schema

# --- !Ups
BEGIN;
ALTER TABLE themes
    RENAME _id TO id;
ALTER TABLE themes
    RENAME languageid TO language_id;
ALTER TABLE themes
    RENAME inlanguage TO in_language;
ALTER TABLE themes
    RENAME lastupdate TO last_update;

ALTER TABLE words
    RENAME _id TO id;
ALTER TABLE words
    RENAME languageid TO language_id;
ALTER TABLE words
    RENAME themeid TO theme_id;
ALTER TABLE words
    RENAME infrench TO in_french;
ALTER TABLE words
    RENAME sortword TO sort_word;
ALTER TABLE words
    RENAME inlanguage TO in_language;
ALTER TABLE words
    RENAME lastupdate TO last_update;

ALTER TABLE verbs
    RENAME _id TO id;
ALTER TABLE verbs
    RENAME inlanguage TO in_language;
ALTER TABLE verbs
    RENAME lastupdate TO last_update;

ALTER TABLE formstypes
    RENAME _id TO id;
ALTER TABLE formstypes
    ADD in_french VARCHAR;

ALTER TABLE forms
    RENAME _id TO id;
ALTER TABLE forms
    RENAME formtypeid TO form_type_id;
ALTER TABLE forms
    RENAME verbid TO verb_id;
ALTER TABLE forms
    RENAME languageid TO language_id;
ALTER TABLE forms
    RENAME inlanguage TO in_language;
ALTER TABLE forms
    RENAME lastupdate TO last_update;
COMMIT;


# --- !Downs //#C
BEGIN;
ALTER TABLE themes
    RENAME id TO _id;
ALTER TABLE themes
    RENAME language_id TO languageid;
ALTER TABLE themes
    RENAME in_language TO inlanguage;
ALTER TABLE themes
    RENAME last_update TO lastupdate;

ALTER TABLE words
    RENAME id TO _id;
ALTER TABLE words
    RENAME language_id TO languageid;
ALTER TABLE words
    RENAME theme_id TO themeid;
ALTER TABLE words
    RENAME in_french TO infrench;
ALTER TABLE words
    RENAME sort_word TO sortword;
ALTER TABLE words
    RENAME in_language TO inlanguage;
ALTER TABLE words
    RENAME last_update TO lastupdate;

ALTER TABLE verbs
    RENAME id TO _id;
ALTER TABLE verbs
    RENAME in_language TO inlanguage;
ALTER TABLE verbs
    RENAME last_update TO lastupdate;

ALTER TABLE formstypes
    RENAME id TO _id;
ALTER TABLE formstypes
    DROP IF EXISTS in_french;

ALTER TABLE forms
    RENAME id TO _id;
ALTER TABLE forms
    RENAME form_type_id TO formtypeid;
ALTER TABLE forms
    RENAME verb_id TO verbid;
ALTER TABLE forms
    RENAME language_id TO languageid;
ALTER TABLE forms
    RENAME in_language TO inlanguage;
ALTER TABLE forms
    RENAME last_update TO lastupdate;
COMMIT;

# #ch05-anorm-schema