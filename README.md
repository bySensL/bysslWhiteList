BysslWhiteList just a plugin for minecraft server, where online mode is false. Whitelist using database mysql.

### 1. Create a table before starting.
```
CREATE TABLE `tablename` (
  `id` int NOT NULL,
  `user` varchar(100) NOT NULL,
  `dateTime` datetime NOT NULL,
  `included` tinyint(1) NOT NULL
)
```
### 2. After starting the server, change the config.yml file and enter there:
  #### 2.1. IP address of the host (dbhost)
  #### 2.2. Database User (dbuser)
  #### 2.3. User password (dbpassword)
  #### 2.4. The name of your database (db)
  #### 2.5. The name of the table (tablename)
