CREATE TABLE IF NOT EXISTS phenotype (
  id                 VARCHAR(255) NOT NULL,
  created_date       BINARY(255),
  last_modified_date BINARY(255),
  phenotype_group    VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS property (
  id                 VARCHAR(255) NOT NULL,
  created_date       BINARY(255),
  description        TEXT         NOT NULL,
  last_modified_date BINARY(255),
  meaning            VARCHAR(255) NOT NULL,
  type               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS users (
  email VARCHAR(255) NOT NULL,
  role  VARCHAR(255),
  PRIMARY KEY (email)
);