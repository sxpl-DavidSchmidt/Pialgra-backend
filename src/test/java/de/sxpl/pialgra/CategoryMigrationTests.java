package de.sxpl.pialgra;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.sql.DriverManager;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryMigrationTests {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void existingSchemaIsBaselinedAndExistingCategoriesArePreserved(boolean colorAlreadyExists) throws Exception {
        String url = "jdbc:h2:mem:migration_" + UUID.randomUUID()
                + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        try (var connection = DriverManager.getConnection(url, "sa", "")) {
            String schema;
            try (var input = getClass().getResourceAsStream("/db/migration/V0__initial_schema.sql")) {
                schema = new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
            try (var statement = connection.createStatement()) {
                statement.execute(schema);
                if (colorAlreadyExists) {
                    statement.execute("ALTER TABLE categories ADD COLUMN color VARCHAR(255)");
                }
                statement.execute("INSERT INTO categories (uuid, name) VALUES ('00000000-0000-0000-0000-000000000001', 'Existing')");
            }
            if (colorAlreadyExists) {
                try (var statement = connection.createStatement()) {
                    statement.execute("UPDATE categories SET color = '#12AbEF'");
                }
            }
            Flyway flyway = Flyway.configure().dataSource(url, "sa", "")
                    .baselineOnMigrate(true).baselineVersion("0")
                    .validateMigrationNaming(true).load();
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
            try (var statement = connection.createStatement();
                 var rows = statement.executeQuery("SELECT name, color FROM categories")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getString("name")).isEqualTo("Existing");
                assertThat(rows.getString("color")).isEqualTo(colorAlreadyExists ? "#12AbEF" : null);
            }
            assertThat(flyway.migrate().migrationsExecuted).isZero();
        }
    }
}
