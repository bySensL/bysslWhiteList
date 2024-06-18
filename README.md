Плагин на белый список, использующий базу данных mysql.

### 1. Перед запуском сервера, создайте таблицу, данной командой:
```
CREATE TABLE `tablename` (
  `id` int NOT NULL,
  `user` varchar(100) NOT NULL,
  `dateTime` datetime NOT NULL,
  `included` tinyint(1) NOT NULL
)
```
### 2. После запуска сервера, поменяйте в файле config.yml следующие переменные:
  #####   2.1. dbhost - айпи адрес хоста бд
  #####   2.2. dbuser - имя пользователя в бд
  #####   2.3. dbpassword - пароль пользователя
  #####   2.4. db - название бд
  #####   2.5. tablename - название таблицы
