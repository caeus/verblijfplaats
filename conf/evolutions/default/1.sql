# --- !Ups
CREATE TABLE "country" (
  "id"             BIGINT     NOT NULL,
  "code"           VARCHAR    NOT NULL PRIMARY KEY,
  "name"           VARCHAR    NOT NULL,
  "continent"      VARCHAR    NOT NULL,
  "wikipedia_link" VARCHAR    NOT NULL,
  "keywords"       TEXT ARRAY NOT NULL
);
CREATE TABLE "airport" (
  "id"                BIGINT           NOT NULL PRIMARY KEY,
  "ident"             VARCHAR          NOT NULL,
  "kind"              VARCHAR          NOT NULL,
  "name"              VARCHAR          NOT NULL,
  "latitude_deg"      DOUBLE PRECISION NOT NULL,
  "longitude_deg"     DOUBLE PRECISION NOT NULL,
  "elevation_ft"      DOUBLE PRECISION,
  "continent"         VARCHAR          NOT NULL,
  "iso_country"       VARCHAR          NOT NULL,
  "iso_region"        VARCHAR          NOT NULL,
  "municipality"      VARCHAR,
  "scheduled_service" BOOLEAN          NOT NULL,
  "gps_code"          VARCHAR,
  "iata_code"         VARCHAR,
  "local_code"        VARCHAR,
  "home_link"         VARCHAR,
  "wikipedia_link"    VARCHAR,
  "keywords"          TEXT ARRAY       NOT NULL
);
CREATE TABLE "runway" (
  "id"                        BIGINT           NOT NULL PRIMARY KEY,
  "airport_ref"               BIGINT           NOT NULL,
  "airport_ident"             VARCHAR          NOT NULL,
  "length_ft"                 DOUBLE PRECISION,
  "width_ft"                  DOUBLE PRECISION,
  "surface"                   VARCHAR,
  "lighted"                   DOUBLE PRECISION NOT NULL,
  "closed"                    DOUBLE PRECISION NOT NULL,
  "le_ident"                  VARCHAR,
  "le_latitude_deg"           DOUBLE PRECISION,
  "le_longitude_deg"          DOUBLE PRECISION,
  "le_elevation_ft"           DOUBLE PRECISION,
  "le_heading_degT"           DOUBLE PRECISION,
  "le_displaced_threshold_ft" DOUBLE PRECISION,
  "he_ident"                  VARCHAR,
  "he_latitude_deg"           DOUBLE PRECISION,
  "he_longitude_deg"          DOUBLE PRECISION,
  "he_elevation_ft"           DOUBLE PRECISION,
  "he_heading_degT"           DOUBLE PRECISION,
  "he_displaced_threshold_ft" DOUBLE PRECISION
);
# --- !Downs
DROP TABLE "runway";
DROP TABLE "airport";
DROP TABLE "country";
