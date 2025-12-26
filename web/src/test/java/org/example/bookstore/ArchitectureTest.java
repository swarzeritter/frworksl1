package org.example.bookstore;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "org.example.bookstore")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_only_be_in_web = 
        classes()
            .that().haveSimpleNameEndingWith("Servlet")
            .should().resideInAPackage("..web..");

    @Test
    void testNoCyclicDependencies() {
        JavaClasses classes = new ClassFileImporter().importPackages("org.example.bookstore");
        
        slices()
            .matching("org.example.bookstore.(*)..")
            .should().beFreeOfCycles()
            .check(classes);
    }
}
