# bookstore
Simple book store app which emulates adding products to a cart and commiting purchase


## Installation & Run
_run cmds_

./mvnw clean package

docker compose up --build


## Database
[liquibase changesets](src/main/resources/db/changelog/init-storedata-changelog.xml)

## Run tests
_**Module**_

./mvnw test

_**Integration**_

./mvnw verify

## Build
./mvnw package