# $Revision$
# $Date$

INSERT INTO ofVersion (name, version) VALUES ('LogThemAll', 0);

CREATE TABLE ofLogThemAll (
  timestamp     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  logID         BIGINT      NOT NULL AUTO_INCREMENT ,
  packet        TEXT        NOT NULL ,
  session       TEXT        NOT NULL ,
  incoming      BOOLEAN     NOT NULL ,
  processed     BOOLEAN     NOT NULL ,
  PRIMARY KEY (logID)
);
