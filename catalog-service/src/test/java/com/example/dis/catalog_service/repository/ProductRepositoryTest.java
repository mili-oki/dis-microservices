package com.example.dis.catalog_service.repository;

import com.example.dis.catalog_service.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
// eksplicitni scan – ne škodi ni kad je sve već ispod root paketa
@EntityScan(basePackageClasses = Product.class)
@EnableJpaRepositories(basePackageClasses = ProductRepository.class)
// isključi SQL init + migracije u okviru ovog testa
@ImportAutoConfiguration(exclude = {
        SqlInitializationAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
// i dodatno, kroz properties (dupli osigurač)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repo;

    @Test
    void saves_and_reads_product() {
        Product p = new Product();
        p.setName("Item");
        p.setPrice(new BigDecimal("12.34"));
        p.setStock(5);

        Product saved = repo.save(p);
        assertThat(saved.getId()).isNotNull();

        Product loaded = repo.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getName()).isEqualTo("Item");
        assertThat(loaded.getPrice()).isEqualByComparingTo("12.34");
        assertThat(loaded.getStock()).isEqualTo(5);
    }

    @Test
    void findByNameContainingIgnoreCase_returns_partial_case_insensitive_matches() {
        repo.save(product("Laptop", "999.99", 10));
        repo.save(product("lap desk", "19.99", 50));
        repo.save(product("MOUSE", "9.99", 100));

        List<Product> lap = repo.findByNameContainingIgnoreCase("lap");
        assertThat(lap).extracting(Product::getName)
                       .containsExactlyInAnyOrder("Laptop", "lap desk");

        List<Product> mouse = repo.findByNameContainingIgnoreCase("mouse");
        assertThat(mouse).extracting(Product::getName)
                         .containsExactly("MOUSE");
    }

    private static Product product(String name, String price, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        return p;
    }
}
