package org.example.bookstore;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "org.example.bookstore")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_servlet = 
        noClasses()
            .that().resideInAPackage("..persistence..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.servlet..", "javax.servlet..");

    @ArchTest
    static final ArchRule repositories_should_only_be_in_persistence = 
        classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().haveSimpleNameNotContaining("Port")
            .should().resideInAPackage("..persistence..");

    @Test
    void testPersistenceIndependence() {
        JavaClasses classes = new ClassFileImporter().importPackages("org.example.bookstore.persistence");
        
        noClasses()
            .that().resideInAPackage("..persistence..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.servlet..")
            .check(classes);
    }
}

