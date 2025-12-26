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
    static final ArchRule core_should_not_depend_on_servlet = 
        noClasses()
            .that().resideInAnyPackage("..domain..", "..port..", "..service..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.servlet..", "javax.servlet..");

    @ArchTest
    static final ArchRule core_should_not_depend_on_jdbc = 
        noClasses()
            .that().resideInAnyPackage("..domain..", "..port..", "..service..")
            .should().dependOnClassesThat().resideInAnyPackage("java.sql..", "javax.sql..");

    @Test
    void testCoreIndependence() {
        JavaClasses classes = new ClassFileImporter().importPackages("org.example.bookstore");
        
        noClasses()
            .that().resideInAnyPackage("..domain..", "..port..", "..service..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.servlet..", "java.sql..")
            .check(classes);
    }
}
