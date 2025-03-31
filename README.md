# DDD

[![javadoc](https://javadoc.io/badge2/com.helger/ddd/javadoc.svg)](https://javadoc.io/doc/com.helger/ddd)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.helger/ddd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.helger/ddd) 

Document Details Determinator - determine [VESID](https://github.com/phax/ph-diver)s from payload to use with https://github.com/phax/phive and https://github.com/phax/phive-rules

This project helps to determine the VESID from arbitrary payloads.
Currently only XML payloads are supported.

You can test DDD via a web frontend at https://peppol.helger.com/public/locale-en_US/menuitem-tools-ddd

This project is part of my Peppol solution stack. See https://github.com/phax/peppol for other components and libraries in that area.

# Concept

A set of relevant fields for business documents is defined. Currently these are (see enum `EDDDSourceField`):
* Customization ID
* Process ID
* Business Document ID
* Sender ID (comprised of scheme and value)
* Sender Name
* Sender Country Code - added in v0.1.4
* Receiver ID (comprised of scheme and value)
* Receiver Name
* Receiver Country Code - added in v0.1.4

These fields are to be determined differently depending on a specific syntax (see class `DDDSyntax`).
Each syntax is uniquely determined by the combination of the XML root element namespace URI and local name.

DDD offers a mapping of the above mentioned fields on a set of predefined syntaxes (in alphabetical order):
* CII D16B (ID `cii-d16b`)
* fatturaPA 1.2 (ID `fatturapa-12`) - added in v0.3.2
* Peppol End User Statistics Report (ID `peppol-eusr`) - added in v0.2.0
* Peppol Transaction Statistics Report (ID `peppol-tsr`) - added in v0.2.0
* UBL 2.x Application Response (ID `ubl2-applicationresponse`) - added in v0.1.4
* UBL 2.x Catalogue (ID `ubl2-catalogue`) - added in v0.3.0
* UBL 2.x Catalogue Deletion (ID `ubl2-cataloguedeletion`) - added in v0.3.0
* UBL 2.x Catalogue Item Specification Update (ID `ubl2-catalogueitemspecificationupdate`) - added in v0.3.0
* UBL 2.x Catalogue Pricing Update (ID `ubl2-cataloguepricingupdate`) - added in v0.3.0
* UBL 2.x Catalogue Request (ID `ubl2-cataloguerequest`) - added in v0.3.0
* UBL 2.x Credit Note (ID `ubl2-creditnote`)
* UBL 2.x Despatch Advice (ID `ubl2-despatchadvice`) - added in v0.1.1
* UBL 2.x Invoice (ID `ubl2-invoice`)
* UBL 2.x Order (ID `ubl2-order`) - added in v0.1.1
* UBL 2.x Order Cancellation (ID `ubl2-ordercancellation`) - added in v0.3.0
* UBL 2.x Order Change (ID `ubl2-orderchange`) - added in v0.3.0
* UBL 2.x Order Response (ID `ubl2-orderresponse`) - added in v0.1.1
* UBL 2.x Order Response Simple (ID `ubl2-orderresponsesimple`) - added in v0.3.0
* UBL 2.x Reminder (ID `ubl2-reminder`) - added in v0.3.0
* UBL 2.x Statement (ID `ubl2-statement`) - added in v0.3.0
* UBL 2.x Utility Statement (ID `ubl2-utilitystatement`) - added in v0.3.0
* ZUGFeRD 1.0 (ID `zugferd1`) - added in v0.4.3

The goal is to deduce certain (missing) values based on other values (class `DDDValueProviderPerSyntax`).
The deducible values are currently (in enum `EDDDDeterminedField`):
* Process ID (especially for CII based syntaxes)
* VESID (for selecting the correct validation rules) - see https://github.com/phax/ph-diver for details
* Syntax Version (especially for UBL, where the same namespace URI and local element name is shared)
* Profile Name (mainly to indicate what was found - should be globally unique; added in v0.1.2)

# How to use it

The entry point is the class `DocumentDetailsDeterminator` which requires an instance of `DDDSyntaxList` and an instance of `DDDValueProviderList`.
DDD ships with a predefined list of syntax mappings. The default syntax mapping is created via `DDDSyntaxList.getDefaultSyntaxList ()`.
DDD also ships with a predefined list of value providers. The default value provider is created via `DDDValueProviderList.getDefaultValueProviderList ()`.
Both syntax mapping and value providers are read-only objects and may only be created once.

So the simplest way to create a new `DocumentDetailsDeterminator` is like this:

```java
final DocumentDetailsDeterminator aDDD = 
   new DocumentDetailsDeterminator (DDDSyntaxList.getDefaultSyntaxList (),
                                    DDDValueProviderList.getDefaultValueProviderList ());
```

This object can be used to determine multiple document details via the following API:

```java
final Element aRootElement = ...;
final DocumentDetails aDD = aDDD.findDocumentDetails (aRootElement);
// aDD may be null
```

The variable `aRootElement` indicates a `org.w3c.dom.Element` node representing the document to be determined.
The resulting details are in the object `aDD` of type `DocumentDetails`.

# Syntaxes

DDD is about determining XML document types, so the determination of the overall syntax is based on the
  root element namespace URI and local element name.
  
So e.g. for the following file snippet
```xml
<Invoice xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
    xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
    xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2">
  <cbc:CustomizationID>urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0</cbc:CustomizationID>
  ...    
</Invoice>
```

The root element namespace URI is `urn:oasis:names:specification:ubl:schema:xsd:Invoice-2` and the local element name is `Invoice`.

Each syntax has the following properties:
* an internal identifier (e.g. `ubl2-invoice` for the UBL 2.1 Invoice)
* a display name for the syntax (e.g. `UBL 2.x Invoice`)  
* a set of XPath expressions to determine the generic document details fields. Not each syntax of course needs to support all the fields. Supported fields are:

| Field                 | ID                    |
| --------------------- | --------------------- |
| Customization ID      | `CustomizationID`     |
| Process ID            | `ProcessID`           |
| Business Document ID  | `BusinessDocumentID`  |
| Sender ID Scheme      | `SenderIDScheme`      |
| Sender ID Value       | `SenderIDValue`       |
| Sender Name           | `SenderName`          |
| Sender Country Code   | `SenderCountryCode`   |
| Receiver ID Scheme    | `ReceiverIDScheme`    |
| Receiver ID Value     | `ReceiverIDValue`     |
| Receiver Name         | `ReceiverName`        |
| Receiver Country Code | `ReceiverCountryCode` |

The XML Schema for syntax list can be found at https://github.com/phax/ddd/tree/main/src/main/resources/schemas

## Adding custom syntaxes

The default syntax list is included inside DDD.
It can be accessed via `DDDSyntaxList.getDefaultSyntaxList ()`.

To add new syntaxes, you need to provide your own rule file, and read it via `DDDSyntaxList.readFromXML`.
Afterwards, the default list and the newly read syntax list can be merged via `DDDSyntaxList.createMergedSyntaxList`.

Please note that when merging syntax lists, each syntax must be unique. There is no overwriting, just adding of syntaxes.

# Value Providers

The DDD Value Providers determines the specific "profile" or "subsets" of a syntax, based on one or more specific values.

E.g. for the syntax "UBL 2.x Invoice", if the `CustomizationID` has the value `urn:cen.eu:en16931:2017#compliant#urn:xeinkauf.de:kosit:xrechnung_3.0` than
  it is an "XRechnung UBL Invoice 3.0" which uses syntax version (UBL) `2.1` and the VESID for validation is `de.xrechnung:ubl-invoice:3.0.2`.

The XML Schema for value provider list can be found at https://github.com/phax/ddd/tree/main/src/main/resources/schemas

## Flags

Some determined details have so called `flags` as indicator to generic use cases. 
Each details can have 0 to unbounded entries.
They were introduced in DDD v0.5.0.

Example for actually used flags:
* **`IsEN16931-2017CIUS`** - indicates that a document type is a CIUS to EN 16931:2017

## Adding custom value providers

The default syntax list is included inside DDD.
It can be accessed via `DDDValueProviderList.getDefaultValueProviderList ()`.

To add new value providers, you need to provide your own rule file, and read it via `DDDValueProviderList.readFromXML`.
Afterwards, the default list and the newly read value provider list can be merged via `DDDValueProviderList.createMergedValueProviderList`.

Please note that when merging value provider lists, the lists are woven into each other. 
  However, specific profiles existing in one list cannot be overwritten by the other list.
  The lists can only extend each other.

# Maven usage

Add the following to your `pom.xml` to use this artifact, replacing `x.y.z` with the latest version:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ddd</artifactId>
  <version>x.y.z</version>
</dependency>
```

# News and noteworthy

* v0.5.2 - 2025-03-31
    * Added support for Peppol PINT AE
* v0.5.1 - 2025-03-24
    * Added support for Peppol BIS Self-Billing
* v0.5.0 - 2025-01-28
    * Added the mapping of the "ProcessID" field for the `ciid16b` syntax
    * Added a new element `flag` into the Value Provider list to be able to classify document types
    * Updated Value Provider XSD from 1.0 to 1.1
    * Extracted classes `DocumentDetailsXMLHelper` and `DocumentDetailsJsonHelper`
    * Moved the public XML element names from `DocumentDetails` to `DocumentDetailsXMLHelper`
    * Added a reverse conversion from JSON and XML back to `DocumentDetails`
    * Implemented `DocumentDetails.equals` and `hashCode`
* v0.4.5 - 2025-01-08
    * XRechnung Extension VESIDs were updated to match phive-rules 3.2.4
    * XRechnung UBL CreditNote has no Extension document types
* v0.4.4 - 2025-01-03
    * Added support for Customization IDs of Factur-X 1.0.x and ZUGFeRD 2.x (requires phive-rules 3.2.3)
* v0.4.3 - 2025-01-02
    * Added support for the custom ZUGFeRD 1.0 syntax
* v0.4.2 - 2024-12-10
    * Fixed the process identifier for "EN 16931 CII"
* v0.4.1 - 2024-12-05
    * Fixed XRechnung 3.x CII VESID to use `3.0.2` instead of `3.0.1`
    * Added support for plain EN 16931 CII detection
    * Switched the pseudo version of all Peppol VESIDs from `latest` to `latest-active`
    * Added the possibility to convert the `DocumentDetails` to a standard XML
* v0.4.0 - 2024-09-13
    * Added support for Peppol PINT A-NZ BIS (billing and self-billing)
    * Fixed typo in VESID of "Peppol PINT Japan Credit Note"
    * Added XML Schemas for syntax list and value providers - therefore requiring JAXB as a dependency
* v0.3.2 - 2024-07-29
    * Added "syntax ID" to `DocumentDetails`
    * Added fatturaPA support. See [#1](https://github.com/phax/ddd/issues/1) - thx @jstaerk
    * Added support for Peppol PINT A-NZ (billing and self-billing)
    * Added support for Peppol PINT Japan
    * Added support for Peppol PINT Malaysia
    * Added support for Peppol PINT Singapore
* v0.3.1 - 2024-07-04
    * Added support to merge `DDDValueProviderList` objects via `createMergedValueProviderList`
* v0.3.0 - 2024-04-06
    * Extended syntax list
    * Added Customization ID and Syntax Version into `DocumentDetails`
    * Added detection of formats based on multiple conditions
* v0.2.1 - 2024-02-06
    * Fixed a typo in the Customization of the Peppol BIS Despatch Advice
* v0.2.0 - 2024-01-18
    * Split enum `EDDDField` into `EDDDSourceField` and `EDDDDeterminedField`
    * Fixed error in XRechnung 2.2 VESIDs
    * Made all `EDDDSourceField` items optional, except for CustomizationID
    * Added support for Peppol EUSR and Peppol TSR
    * Added the missing VESID for UBL Orders
* v0.1.4 - 2024-01-08
    * Extended syntax list - added UBL 2.x Application Response
    * Added support for Peppol MLR
    * Added support for Sender Country Code and Receiver Country Code fields 
* v0.1.3 - 2023-12-06
    * Fixed XRechnung 3.0 detection 
    * Added support for XRechnung 1.2
    * Added support for all XRechnung Extension versions
* v0.1.2 - 2023-11-13
    * Added new element `ProfileName`
    * Extended value provider mapping for "UBL 2.x Order Response"
    * Added builder to `DocumentDetails` class
* v0.1.1 - 2023-11-13
    * Extended syntax list - added "UBL 2.x Despatch Advice", "UBL 2.x Order" and "UBL 2.x Order Response"
    * Improved customizability of `DocumentDetailsDeterminator`
* v0.1.0 - 2023-09-19
    * Initial version supporting the following syntaxes: "UBL 2.x Invoice", "UBL 2.x Credit Note" and "CII D16B"

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
