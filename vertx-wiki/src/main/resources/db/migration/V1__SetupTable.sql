create table Pages (
  ID INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  NAME varchar(255) unique,
  CONTENT TEXT
)