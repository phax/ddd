# Task: split `ddd` into `ddd-model` and `ddd`

Status: **implemented, 2026-09-28** as version `0.9.0-SNAPSHOT`. T1-T10 and T12 are
done; T11 (CI) can only be confirmed on the first push. See the note under T8 -
the measured saving is smaller than this document originally assumed.

## Why

Consumers that only need to *carry* or *deserialize* a `DocumentDetails` — REST
clients, message brokers, anything that receives a determination made elsewhere —
currently have to depend on the whole determinator: the JAXB syntax/value-provider
model, the XPath machinery, the SBDH/XHE unwrappers and `peppol-id` with its
BDXR-SMP / XAdES / XMLDSig XSD artifacts behind it.

The trigger is the new `phorm-client` library, which receives `DocumentDetails`
as JSON from the phorm REST API and never determines anything locally. See
`../phorm-client/docs/lightweight-model-analysis.md` for the full measurement
across `ddd` and `phive`.

Measured cost of the coupling (resolved with `mvn dependency:build-classpath`,
summing the resulting JARs, `ddd` 0.8.10):

| Dependency set | JARs | Size |
|---|---:|---:|
| `ph-httpclient` + `ph-json` + `ph-xml` | 23 | 4.9 MB |
| … + `ddd` (whole) | 37 | 6.0 MB |
| … + `edelivery-id` only (what the model actually needs) | 29 | 5.3 MB |

**Saving: 8 JARs / 0.7 MB**, and — more importantly — it removes the SMP and
XML-signature datatypes from the classpath of anything that just wants the value
object.

## The cut is already clean

Verified against the current sources:

| Class | References inside `com.helger.ddd` | Needs |
|---|---|---|
| `DocumentDetails` | none | `IParticipantIdentifier`, `IDocumentTypeIdentifier`, `IProcessIdentifier` → **`edelivery-id`** |
| `DocumentDetailsJsonHelper` | none | `IIdentifierFactory` → **`edelivery-id`** |
| `DocumentDetailsXMLHelper` | none | `IIdentifierFactory` → **`edelivery-id`** |
| `DocumentDetailsDeterminator` | uses `DocumentDetails` | `PeppolIdentifierFactory`, `PeppolDocumentTypeIdentifierParts`, `PeppolIdentifierHelper` → **`peppol-id`** |
| `IDDDDocumentUnwrapper` | uses `DocumentDetails` | — |

The three model classes reference nothing else in the library — not even by
same-package access. The arrows point one way (`ddd` → `ddd-model`), so there is
no cycle to break and no code to rewrite.

`DocumentDetailsJsonHelper.getAsDocumentDetails` and
`DocumentDetailsXMLHelper.getAsDocumentDetails` already take the
`IIdentifierFactory` as a parameter, so a model-only consumer passes
`SimpleIdentifierFactory` — which lives in `edelivery-id`.

## Target layout

```
ddd/                       com.helger:ddd-parent-pom      (pom, aggregator)
├── ddd-model/             com.helger:ddd-model           (jar)
│     DocumentDetails, DocumentDetailsJsonHelper, DocumentDetailsXMLHelper
│     -> ph-xml, ph-json, edelivery-id
└── ddd/                   com.helger:ddd                 (jar, unchanged coordinates)
      DocumentDetailsDeterminator, DDDVersion, IDDDDocumentUnwrapper*,
      model/, unwrap/, all resources, the JAXB build
      -> ddd-model, ph-jaxb, ph-jaxb-adapter, peppol-id
```

`com.helger:ddd` keeps its coordinates and gains `ddd-model` as a transitive
dependency, so every current consumer keeps building untouched. Verified
consumers in the local workspace: `phorm`, `phoss-ap`, `ph-redact`,
`peppol-shared-ui`, `meta/deps`.

## Decisions to confirm before starting

1. **groupId.** The ecosystem convention for multi-module repos is a dedicated
   groupId (`com.helger.phive`, `com.helger.schematron`, `com.helger.peppol`).
   Applying it here would rename `com.helger:ddd` and break all five consumers.
   Proposed: **keep `com.helger`**, name the aggregator `com.helger:ddd-parent-pom`.
2. **Split package.** `com.helger.ddd` would span both JARs. That is fine on the
   classpath and `ddd` ships no OSGi headers, no `Automatic-Module-Name` and no
   `module-info` today (checked in `target/ddd-0.8.11-SNAPSHOT.jar`), so nothing
   breaks now. The alternative — moving the model classes to a new package — is
   an API break for all consumers. Proposed: **accept the split package**, and
   record it so a future JPMS/OSGi push knows about it.
3. **Version.** Current is `0.8.11-SNAPSHOT` with an open "work in progress"
   news entry. A module split is structural. Proposed: **`0.9.0-SNAPSHOT`**,
   moving the two existing v0.8.11 bullets to the new entry.

---

## Tasks

### T1 — Turn the root POM into the aggregator

- `artifactId` `ddd` → `ddd-parent-pom`, `<packaging>jar</packaging>` → `pom`,
  `<name>` accordingly. Keep `<parent>` = `com.helger:parent-pom:3.1.0`.
- Add `<modules>`: `ddd-model`, `ddd`.
- Keep the existing `<dependencyManagement>` imports (`ph-commons-parent-pom`
  12.5.0, `peppol-commons-parent-pom` 13.0.0) and the `phive-rules.version`
  property (used by the `ddd` module's test scope).
- Add `<dependencyManagement>` entries for `com.helger:ddd-model` and
  `com.helger:ddd`, both at `${project.version}` — mirroring `phive/pom.xml`.
- Remove `<dependencies>`, the JAXB plugin execution and the javadoc
  `<sourcepath>` override from the root; they belong to the `ddd` module (T5).

*Done when* `mvn -q validate` resolves both modules from the root.

### T2 — Create the `ddd-model` module

- `ddd-model/pom.xml`, parent `com.helger:ddd-parent-pom:${project.version}`,
  packaging `jar`, `<url>https://github.com/phax/ddd/ddd-model</url>`,
  `<inceptionYear>2023</inceptionYear>`, the same `<licenses>`,
  `<organization>` and `<developers>` blocks as the other modules (see
  `phive/phive-api/pom.xml` for the shape).
- Dependencies: `ph-xml`, `ph-json`, `com.helger.peppol:edelivery-id`.
- Test dependencies: `junit`, `slf4j-simple`, `ph-unittest-support-ext`.
- No JAXB plugin, no resource filtering — this module has no resources.

*Done when* `ddd-model` builds standalone and its dependency tree contains
neither `peppol-id` nor `ph-jaxb`.

### T3 — Move the three classes and their tests

Main (`git mv`, no source edits — no imports change, the package stays
`com.helger.ddd`):

- `DocumentDetails.java`
- `DocumentDetailsJsonHelper.java`
- `DocumentDetailsXMLHelper.java`

Tests:

- `DocumentDetailsTest.java`
- `DocumentDetailsJsonHelperTest.java`
- `DocumentDetailsXMLHelperTest.java`

All three tests use only `SimpleIdentifierFactory` / `IIdentifierFactory` and
read no test resources, so they move as-is with nothing else.

Everything else stays in `ddd`, explicitly including `DDDVersion` and
`ddd-version.properties` — two filtered `ddd-version.properties` at the root of
two JARs would collide on the classpath, and `DDDVersion` is referenced by
nothing in main.

*Done when* both modules compile and all tests pass in their new homes.

### T4 — Re-wire the `ddd` module POM

- Move the current root `<dependencies>` into `ddd/pom.xml`.
- Add `com.helger:ddd-model` (version from the aggregator's
  `dependencyManagement`).
- Keep `ph-jaxb`, `ph-jaxb-adapter`, `peppol-id` and the `phive-rules-all` test
  dependency. Keep `ph-xml` and `ph-json` declared explicitly — they are used
  directly by `model/` and `unwrap/`, not only inherited through `ddd-model`.

*Done when* `mvn clean install` is green from the root.

### T5 — Distribute the build configuration

- `ddd/pom.xml` keeps: the JAXB plugin execution (`schemaDirectory`
  `${basedir}/src/main/resources/schemas`, `bindingDirectory`
  `${basedir}/src/main/jaxb`, all `-Xph-*` args), the `<resources>` filtering
  block for `**/*.properties`, and the javadoc `<sourcepath>` override that adds
  `${project.build.directory}/generated-sources/xjc`.
- Move the `forbiddenapis` configuration (signatures artifact
  `com.helger:ph-forbidden-apis:1.1.1`, `forbidden-apis-java9.txt`) to the
  aggregator's `<build><plugins>` so both modules inherit it.
- `ddd-model` needs neither the JAXB plugin nor the javadoc sourcepath override.

*Done when* `mvn clean install` still fails on a banned Java 9+ signature in
*either* module (spot-check by temporarily introducing one).

### T6 — Per-module `src/etc`

`parent-pom` references the license header template by the relative path
`src/etc/license-template.txt`, which resolves per module. `phive` keeps a copy
in every module for exactly this reason.

- `ddd-model/src/etc/license-template.txt` — copy of the existing file.
- `ddd-model/src/etc/javadoc.css` — copy of the existing file.
- Move the existing `src/etc/*` into `ddd/src/etc/`, and keep
  `license-template.txt` at the aggregator root as well (matching `phive/src/etc`).

*Done when* `mvn license:check` (or the build's license phase) passes for both
modules.

### T7 — Per-module `LICENSE` / `NOTICE` resources

`src/main/resources/LICENSE` and `src/main/resources/NOTICE` are packaged into
the JAR. Copy both into `ddd-model/src/main/resources/` so its artifact carries
them too.

*Done when* both JARs contain `LICENSE` and `NOTICE`.

### T8 — Verify the saving

This is the point of the exercise, so measure it rather than assume it.

```bash
mvn -o dependency:tree -pl ddd-model
```

*Done when* the `ddd-model` tree contains `edelivery-id` and contains **no**
`peppol-id`, `ph-xsds-bdxr-smp1`, `ph-xsds-bdxr-smp2`, `ph-xsds-xades132`,
`ph-xsds-xades141`, `ph-xsds-xmldsig11`, `ph-xsds-ccts-cct-schemamodule` or
`ph-jaxb`.

**The originally stated exclusions and numbers were wrong.** Two corrections,
both measured on 2026-09-28:

1. `edelivery-id` 13.0.0 compile-depends on `peppol-id-datatypes`, which in turn
   pulls `ph-jaxb-adapter` and `ph-xsds-xmldsig`. Those three are therefore
   *always* in the `ddd-model` tree and cannot be excluded. Only `peppol-id` and
   `ph-jaxb` are actually removed.
2. The footprint table at the top of this document was measured against `ddd`
   0.8.10, which pinned `peppol-commons` **12.5.3**. The current sources are on
   `peppol-commons` **13.0.0**, where `peppol-id` is already much slimmer - the
   BDXR-SMP, XAdES, XMLDSig11 and CCTS artifacts have moved out of it upstream.
   Most of the claimed saving had therefore already been realised by that
   upgrade, before this split.

Measured with `mvn dependency:build-classpath` against the local repository
(`ph-httpclient` 11.4.6, `ph-commons` 12.5.0, `peppol-commons` 13.0.0):

| Dependency set | JARs | Size |
|---|---:|---:|
| `ph-httpclient` + `ph-json` + `ph-xml` | 24 | 4.93 MB |
| … + `ddd-model` | 31 | 5.35 MB |
| … + `ddd` (whole) | 34 | 5.67 MB |

**Actual saving: 3 JARs / 0.32 MB** (`ddd`, `peppol-id`, `ph-jaxb`), not the
8 JARs / 0.7 MB claimed above. The split still achieves its structural goal -
a model-only consumer no longer has `peppol-id` or the JAXB runtime on its
classpath - but the byte-level payoff is modest.

### T9 — README

- **Maven usage**: keep the `com.helger:ddd` block, add a second block for
  `com.helger:ddd-model` with one sentence on when to pick which — `ddd-model`
  for carrying or deserializing a `DocumentDetails`, `ddd` for determining one.
- **News and noteworthy**: per the decision in point 3 above, retitle the open
  entry to `v0.9.0 - work in progress`, keep its two existing bullets and add
  one for the split, naming the new artifact and stating that `com.helger:ddd`
  is unchanged for existing users.

### T10 — CLAUDE.md

The Architecture section states "This is a single-module Maven project (parent:
`com.helger:parent-pom`)". Update it to describe the two modules and which
classes live where, and note that `DocumentDetails` and the two serialization
helpers are in `ddd-model`.

### T11 — CI

`.github/workflows` runs `mvn --batch-mode --update-snapshots install` and
`-P release-snapshot deploy` from the repository root, which handles a
multi-module reactor unchanged. Confirm on the first push that both artifacts
are deployed, not just the aggregator.

### T12 — Consumer check

Build each local consumer against the new SNAPSHOT and confirm no POM change is
needed: `phorm`, `phoss-ap`, `ph-redact`, `peppol-shared-ui`, `meta/deps`.

*Done when* all five build green with no edits.

Result: `phorm`, `phoss-ap`, `ph-redact` and `peppol-shared-ui` all build green
with nothing but the `ddd` version bumped to `0.9.0-SNAPSHOT` - no structural
POM change. `meta/deps/pom.xml` is generated from `EProject`, so it is not built
directly; the `EProject` entry was reworked into `DDD_PARENT_POM` +
`DDD_MODEL` + `DDD` instead, mirroring `PHIVE_PARENT_POM`.

---

## Out of scope

- The `phive` side of the same problem (`phive-api` pulling Saxon-HE for one
  enum, `phive-result` pulling five Schematron engines for one method branch).
  Tracked in `../phorm-client/docs/lightweight-model-analysis.md` as S1 and S2;
  those are larger and land in different repositories.
- Any change to the determination logic, the syntax/value-provider data files or
  the public API. This task is a pure packaging split: no class is renamed, no
  package is changed, no method signature moves.
