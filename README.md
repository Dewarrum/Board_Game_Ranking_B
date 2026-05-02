# Board_Game_Ranking

## Local Environment Setup

### Prerequisites
- Java 21
- PostgreSQL running locally

### Default local database
The app and Flyway are configured to use this local DB by default:
- URL: `jdbc:postgresql://localhost:5432/postgres`
- Username: `postgres`
- Password: `password`

You can override datasource settings with:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Optional environment variables
- `JWT_SECRET` (defaults to a local dev secret if omitted)
- `SUPABASE_SERVICE_ROLE_KEY` (required for Supabase storage features)

### Run database migrations (Gradle)
```bash
./gradlew flywayMigrate
```

### Run the application locally
```bash
./run-local.sh
```
