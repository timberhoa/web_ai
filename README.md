### Short guide to run project
## Run container
`docker compose down -v`: remove current volume
`docker compose up -d`: run the container
## Check the db
`docker exec -it mysql_db bash`: go into mysql container bash

`mysql -u root -p password`: enter password for user root
## Run query
`use db_1;`
`show tables;`
