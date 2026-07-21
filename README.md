<!-- markdownlint-disable MD013 -->
# java-fwf

[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-blue?logo=openjdk)](https://www.oracle.com/java/)
[![FWF Compliance](https://img.shields.io/badge/FWF_Compliance-5%2F5_cases_passed-brightgreen?logo=github)](https://github.com/fixed-width-file/fwf-compliance-tests)
[![QA](https://github.com/fixed-width-file/java-fwf/actions/workflows/qa.yml/badge.svg)](https://github.com/fixed-width-file/java-fwf/actions/workflows/qa.yml)
[![Coverage](https://codecov.io/gh/fixed-width-file/java-fwf/branch/main/graph/badge.svg)](https://codecov.io/gh/fixed-width-file/java-fwf)
[![Docs](https://img.shields.io/badge/docs-GitHub%20Pages-blue)](https://fixed-width-file.github.io/java-fwf/)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)

**java-fwf** is a fast, type-safe Java 17+ library for parsing, validating, hydrating, and exporting **Fixed Width Files (FWF)**.

It is part of the [Fixed Width File Ecosystem](https://fixed-width-file.github.io/) and fully implements the **[fwf-compliance-tests v1.0.0](https://github.com/fixed-width-file/fwf-compliance-tests)** specification.

---

## 🌟 Key Features

- **Type-Safe Columns**: `CharColumn`, `RightCharColumn`, `PositiveIntegerColumn`, `PositiveDecimalColumn`, `DateColumn`, `TimeColumn`, and `DateTimeColumn`.
- **Flexible Descriptors**: Structured records with `HeaderRowDescriptor`, `DetailRowDescriptor`, and `FooterRowDescriptor`.
- **Cross-Language Hydration**: Dehydrate/Hydrate descriptors to/from JSON representations compatible with Python `pyfwf` and PHP `php-fwf`.
- **Multiple Output Renders**: Export layout specifications to Markdown, ReStructuredText (RST), or HTML tables via `RenderUtils`.
- **Full Test Suite & Coverage**: Verified line (>95%) and branch (>85%) coverage via JaCoCo and JUnit 5.
- **Git Hooks Ready**: Pre-configured `pre-commit` and `pre-push` hooks.

---

## 🚀 Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.kelsoncm</groupId>
    <artifactId>java-fwf</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.kelsoncm:java-fwf:1.0.0'
```

---

## 💡 Quickstart

```java
import com.kelsoncm.fwf.columns.*;
import com.kelsoncm.fwf.descriptors.*;
import com.kelsoncm.fwf.readers.Reader;

import java.util.List;
import java.util.Map;

public class Quickstart {
    public static void main(String[] args) {
        // 1. Define columns
        CharColumn nameCol = new CharColumn("name", 20, "User Name");
        PositiveIntegerColumn ageCol = new PositiveIntegerColumn("age", 3, "Age in years");

        // 2. Define row and file descriptors
        DetailRowDescriptor detail = new DetailRowDescriptor(List.of(nameCol, ageCol));
        FileDescriptor fileDescriptor = new FileDescriptor(List.of(detail));

        // 3. Read fixed-width file content
        String content = "KELSON MEDEIROS     045\nMARIA SILVA         030\n";
        Reader reader = new Reader(content, fileDescriptor, "\n");

        for (Map<String, Object> row : reader) {
            System.out.println("Name: " + row.get("name") + " | Age: " + row.get("age"));
        }
    }
}
```

---

## 📖 Javadoc & GitHub Pages

Generated Javadoc is published to GitHub Pages:

- **Landing Page**: [docs/index.html](docs/index.html)
- **Javadoc API**: [docs/apidocs/index.html](docs/apidocs/index.html)

---

## 🧪 Testing & Coverage

Run unit tests and JaCoCo coverage check:

```bash
mvn clean test
```

---

## ⚓ Pre-Commit & Pre-Push Setup

Set up pre-commit and pre-push hooks:

```bash
pre-commit install
pre-commit install --hook-type pre-push
```

Run checks manually:

```bash
pre-commit run --all-files
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
