# DDD

Document Details Determinator - determine VESIDs from payload to use with https://github.com/phax/phive and https://github.com/phax/phive-rules

This project helps to determine the VESID from the payload.
Currently only XML payloads are supported.

# Concept

A set of relevant fields for business document is defined. Per version 1.0 these are (enum `EDDDField`):
* Customization ID
* Process ID
* Sender ID
* Receiver ID
* Business Document ID
* Sender Name
* Receiver Name

These fields are to be determined differently depending on a syntax (class `DDDSyntax`).
Each syntax is uniquely determined by the combination of the XML root element namespace URI and local name.

DDD offers a mapping of the above mentioned fields on a set of predefined syntaxes (in alphabetical order):
* CII D16B (ID `cii-d16b`)
* UBL 2.x Credit Note (ID `ubl2-creditnote`)
* UBL 2.x Despatch Advice (ID `ubl2-despatchadvice`)
* UBL 2.x Invoice (ID `ubl2-invoice`)
* UBL 2.x Order (ID `ubl2-order`)
* UBL 2.x Order Response (ID `ubl2-orderresponse`)

And finally certain (missing) values can be deduced based on other values (class `DDDValueProviderPerSyntax`).
The deducible values are currently:
* Process ID (especially for CII based syntaxes)
* VESID (for selecting the correct validation rules) - see https://github.com/phax/ph-diver for details
* Syntax Version (especially for UBL, where the same namespace URI and local element name is shared)

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

* v0.1.1 - 2023-11-13
    * Extended syntax list - added "UBL 2.x Despatch Advice", "UBL 2.x Order" and "UBL 2.x Order Response"
    * Improved customizability of `DocumentDetailsDeterminator`
* v0.1.0 - 2023-09-19
    * Initial version supporting the following syntaxes: UBL 2.x Invoice, UBL 2.x Credit Note and CII D16B

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
