# cschool-todo-http
An example java http web server application for students of "Java basics" class.

Code contains some shortcuts also more sophisticated and/or harder to understand language 
constructions were avoided.

Comments are in Polish to allow students with less experience with English to understand
application fully.

There is additional README_PL.md file with extended description in Polish.

# Before you start application

## Prepare database
* Create user which application will use to connect with database.
```
create user todo_user with password 'todo_user';
```
* Create database giving permission to already prepared user.
```
create database todo with owner todo_user;
```
* Create database schema using script from `src/sql/create.sql` file (script should
be invoked from by `todo_user`).

# Build
```
mvn install
```
# Running tests
```
mvn test 
```
