BysslWhiteList just a plugin for minecraft server, where online mode is false. Whitelist using database mysql.

Create a table before starting.
```CREATE TABLE `tablename` (
  `id` int NOT NULL,
  `user` varchar(100) NOT NULL,
  `dateTime` datetime NOT NULL,
  `included` tinyint(1) NOT NULL
)```
After starting the server, change the config.yml file and enter there
IP address of the host (dbhost)
Database User (dbuser)
User password (dbpassword)
the name of your database (db)
and the name of the table (tablename)
