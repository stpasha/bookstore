# bookstore
Simple book store app which emulates adding products to a cart and commiting purchase


## Installation & Run
_run cmds_

./mvnw clean package

docker compose up --build

### Credentials for test:

user / userpass

admin / adminpass

## Database
[liquibase changesets](src/main/resources/db/changelog/init-storedata-changelog.xml)

## Open API Doc
[api-spec.yaml](bookstore-web/src/main/resources/api-spec.yaml)

## Run tests
_**Module**_

./mvnw test

_**Integration**_

./mvnw verify

## Build
./mvnw package