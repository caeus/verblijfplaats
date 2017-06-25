# --- !Ups
--- I did a second evolution for needed changes in the structure of the db,
--- because loading all data is quite slow
ALTER TABLE "airport"
  ADD CONSTRAINT "airport_country_fk" FOREIGN KEY ("iso_country") REFERENCES "country" ("code") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "runway"
  ADD CONSTRAINT "runway_airport_fk" FOREIGN KEY ("airport_ref") REFERENCES "airport" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
# --- !Downs
ALTER TABLE "runway"
  DROP CONSTRAINT "runway_airport_fk";
ALTER TABLE "airport"
  DROP CONSTRAINT "airport_country_fk";
