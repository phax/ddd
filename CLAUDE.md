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
mvn test -pl ddd -Dtest=DocumentDetailsDeterminatorTest

# Run a single test method
mvn test -pl ddd -Dtest=DocumentDetailsDeterminatorTest#testFindDocumentDetails
```

Requires **Java 17+**. CI tests on Java 17, 21, and 25.

The build runs `forbiddenapis` (against `ph-forbidden-apis-1.1.1`) — using a banned Java 9+ signature will fail the build, not just emit a warning.

## Architecture

This is a multi-module Maven project. The aggregator is `com.helger:ddd-parent-pom` (parent: `com.helger:parent-pom`) and it has two modules:

| Module | Artifact | Contains | Depends on |
|--------|----------|----------|------------|
| `ddd-model` | `com.helger:ddd-model` | `DocumentDetails`, `DocumentDetailsJsonHelper`, `DocumentDetailsXMLHelper` | `ph-xml`, `ph-json`, `edelivery-id` |
| `ddd` | `com.helger:ddd` | `DocumentDetailsDeterminator`, `DDDVersion`, `IDDDDocumentUnwrapper`, `model/`, `unwrap/`, all data files and the JAXB build | `ddd-model`, `ph-jaxb`, `ph-jaxb-adapter`, `peppol-id` |

The value object `DocumentDetails` and the two serialization helpers live in `ddd-model`, so consumers that only carry or deserialize a determination result do not need `peppol-id` or the JAXB machinery. Both modules use the package `com.helger.ddd` — this is an accepted split package; the project ships no OSGi headers, no `Automatic-Module-Name` and no `module-info`.

`com.helger:ddd` keeps its coordinates and depends on `ddd-model`, so existing consumers need no POM change.

### Key classes

| Class | Module | Role |
|-------|--------|------|
| `DocumentDetailsDeterminator` | `ddd` | Main API - resolves XML payloads to `DocumentDetails` |
| `DocumentDetails` | `ddd-model` | Immutable result: syntax ID, source fields, determined fields, flags |
| `DDDSyntaxList` / `DDDSyntax` | `ddd` | Registry of supported XML syntaxes; each syntax maps namespace+root element to XPath expressions for field extraction |
| `DDDValueProviderList` / `DDDValueProviderPerSyntax` | `ddd` | Rule engine that derives VESID/ProcessID/ProfileName from extracted source field values |
| `EDDDSourceField` | `ddd` | Enum of extractable fields (CustomizationID, ProcessID, SenderIDScheme, etc.) |
| `EDDDDeterminedField` | `ddd` | Enum of derived fields (VESID, ProcessID, SyntaxVersion, ProfileName) |
| `DocumentDetailsXMLHelper` / `DocumentDetailsJsonHelper` | `ddd-model` | Serialization to/from XML and JSON |

JAXB classes for the syntax and value-provider models are generated into `ddd/target/generated-sources/xjc/` (packages `com.helger.ddd.model.jaxb.syntax1` and `com.helger.ddd.model.jaxb.vp1`). Do not edit these — change the XSDs in `ddd/src/main/resources/schemas/` and rebuild.

### Value provider rule model (`com.helger.ddd.model`)

`VPSelect` -> `VPIf` -> `VPSourceValue` -> `VPDeterminedValues` / `VPDeterminedFlags` - a tree of conditions matching source field values to determine output fields.

### Data files (`ddd/src/main/resources/ddd/`)

- `syntaxes.xml` - all supported syntax definitions with XPath mappings
- `value-providers.xml` - all value provider rules mapping CustomizationIDs to VESIDs/profiles

Both files carry a `lastmod="YYYY-MM-DD"` attribute on the root element — bump it to the current date when changing either file.

XSD schemas for these files are in `ddd/src/main/resources/schemas/`. JAXB classes are generated from these schemas during build.

### Dependencies

- **ph-commons** - XML/JSON utilities, collection types (`ICommonsList`, `CommonsArrayList`)
- **peppol-commons** - Peppol identifiers (`edelivery-id` for `ddd-model`, `peppol-id` for `ddd`), VESID types
- **JAXB** - XML schema-driven code generation for syntax/value-provider models

## Testing

Tests use JUnit 4 (`@Test` annotations). Test XML documents are in `ddd/src/test/resources/external/<syntax-id>/` with `good/` and `bad/` subdirectories — `DocumentDetailsDeterminatorTest#testReadAllTestfiles` iterates these automatically, so new fixtures only need to be dropped in the right folder.

Key test classes:
- `DocumentDetailsDeterminatorTest` (`ddd`) - end-to-end determination from XML files
- `DDDConsistencyFuncTest` (`ddd`) - validates consistency of syntax and value provider definitions
- `DocumentDetailsTest` / `DocumentDetailsJsonHelperTest` / `DocumentDetailsXMLHelperTest` (`ddd-model`) - value object and serialization round trips, using `SimpleIdentifierFactory`
