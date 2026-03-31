# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DDD (Document Details Determinator) is a Java library that determines VESIDs (Validation Execution Set IDs) from arbitrary XML document payloads. It is part of the [Peppol solution stack](https://github.com/phax/peppol) for electronic invoicing and business document validation.

Entry point: `DocumentDetailsDeterminator` takes an XML `Element`, identifies its syntax (via namespace URI + root element name), extracts source fields (CustomizationID, ProcessID, sender/receiver IDs, etc.), and determines derived fields (VESID, ProfileName, SyntaxVersion) using value provider rules.

## Build Commands

```bash
# Full build with tests
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=DocumentDetailsDeterminatorTest

# Run a single test method
mvn test -Dtest=DocumentDetailsDeterminatorTest#testFindDocumentDetails
```

Requires **Java 17+**. CI tests on Java 17, 21, and 25.

## Architecture

This is a single-module Maven project (parent: `com.helger:parent-pom`).

### Key classes (all in `com.helger.ddd`)

| Class | Role |
|-------|------|
| `DocumentDetailsDeterminator` | Main API - resolves XML payloads to `DocumentDetails` |
| `DocumentDetails` | Immutable result: syntax ID, source fields, determined fields, flags |
| `DDDSyntaxList` / `DDDSyntax` | Registry of supported XML syntaxes; each syntax maps namespace+root element to XPath expressions for field extraction |
| `DDDValueProviderList` / `DDDValueProviderPerSyntax` | Rule engine that derives VESID/ProcessID/ProfileName from extracted source field values |
| `EDDDSourceField` | Enum of extractable fields (CustomizationID, ProcessID, SenderIDScheme, etc.) |
| `EDDDDeterminedField` | Enum of derived fields (VESID, ProcessID, SyntaxVersion, ProfileName) |
| `DocumentDetailsXMLHelper` / `DocumentDetailsJsonHelper` | Serialization to/from XML and JSON |

### Value provider rule model (`com.helger.ddd.model`)

`VPSelect` -> `VPIf` -> `VPSourceValue` -> `VPDeterminedValues` / `VPDeterminedFlags` - a tree of conditions matching source field values to determine output fields.

### Data files (`src/main/resources/ddd/`)

- `syntaxes.xml` - all supported syntax definitions with XPath mappings
- `value-providers.xml` - all value provider rules mapping CustomizationIDs to VESIDs/profiles

XSD schemas for these files are in `src/main/resources/schemas/`. JAXB classes are generated from these schemas during build.

### Dependencies

- **ph-commons** - XML/JSON utilities, collection types (`ICommonsList`, `CommonsArrayList`)
- **peppol-commons** - Peppol identifiers, VESID types
- **JAXB** - XML schema-driven code generation for syntax/value-provider models

## Testing

Tests use JUnit 4 (`@Test` annotations). Test XML documents are in `src/test/resources/external/` organized by syntax type (e.g., `ubl2-invoice/`, `cii-d16b/`).

Key test classes:
- `DocumentDetailsDeterminatorTest` - end-to-end determination from XML files
- `DDDConsistencyFuncTest` - validates consistency of syntax and value provider definitions
