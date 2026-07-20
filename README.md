# java-fwf

[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B%20%2F%2025-orange)](https://jdk.java.net/)
[![FWF Compliance](https://img.shields.io/badge/FWF_Compliance-5%2F5_cases_passed-brightgreen?logo=github)](https://github.com/fixed-width-file/fwf-compliance-tests)
[![Build & Test](https://github.com/fixed-width-file/java-fwf/actions/workflows/ci.yml/badge.svg)](https://github.com/fixed-width-file/java-fwf/actions)
[![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](https://github.com/fixed-width-file/java-fwf)

**java-fwf** is a high-performance, type-safe Java library for parsing, validating, hydrating, and exporting **Fixed Width Files (FWF)**.

It is part of the [Fixed Width File Ecosystem](https://fixed-width-file.github.io/) and fully implements the **[fwf-compliance-tests v1.0.0](https://github.com/fixed-width-file/fwf-compliance-tests)** language-agnostic specification.

---

## 🌟 Key Features

- **Type-Safe Columns**: Support for `CharColumn`, `RightCharColumn`, `PositiveIntegerColumn`, `PositiveDecimalColumn`, `DateColumn`, `TimeColumn`, and `DateTimeColumn`.
- **Flexible Descriptors**: Define structured records with `HeaderRowDescriptor`, `DetailRowDescriptor`, and `FooterRowDescriptor`.
- **Dynamic Hydration**: Dehydrate/Hydrate descriptors and column definitions to/from Map and JSON representations compatible with `python-pyfwf`.
- **Multiple Output Renders**: Export file descriptors directly to Markdown, ReStructuredText (RST), or HTML documentation using `RenderUtils`.
- **100% Code Coverage**: Verified with JaCoCo plugin and JUnit 5 unit test suite.
- **Git Hooks Ready**: Pre-configured `pre-commit` and `pre-push` hooks for code hygiene and automated testing.

---

## 🚀 Installation

Add `java-fwf` as a dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.kelsoncm.fwf</groupId>
    <artifactId>java-fwf</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 💡 Quickstart

```java
import com.kelsoncm.fwf.columns.*;
import com.kelsoncm.fwf.descriptors.*;
import com.kelsoncm.fwf.readers.Reader;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) {
        // 1. Define columns
        CharColumn nameCol = new CharColumn("name", 20, "User Name");
        PositiveIntegerColumn ageCol = new PositiveIntegerColumn("age", 3, "Age in years");

        // 2. Define row and file descriptors
        DetailRowDescriptor detail = new DetailRowDescriptor(List.of(nameCol, ageCol));
        FileDescriptor fileDescriptor = new FileDescriptor(List.of(detail));

        // 3. Read fixed-width content
        String content = "KELSON MEDEIROS     045\nMARIA SILVA         030\n";
        Reader reader = new Reader(content, fileDescriptor, "\n");

        for (Map<String, Object> row : reader) {
            System.out.println("Name: " + row.get("name") + " | Age: " + row.get("age"));
        }
    }
}
```

---

## 🧪 Testing & Compliance

### Running Unit Tests

Run all unit tests and verify compliance against `fwf-compliance-tests`:

```bash
mvn clean test
```

### Checking JaCoCo Code Coverage

To run the test suite with strict JaCoCo 100% coverage verification:

```bash
mvn clean verify
```

---

## ⚓ Pre-Commit & Pre-Push Hooks Setup

This repository uses [pre-commit](https://pre-commit.com/) to enforce code formatting and test verification before commits and pushes.

### 1. Installation

Install `pre-commit` using `pip` or `uv`:

```bash
uv tool install pre-commit
# OR
pip install pre-commit
```

### 2. Register Hooks in Git

Run the following commands in the root of `java-fwf`:

```bash
# Install git pre-commit hook
pre-commit install

# Install git pre-push hook (runs mvn test automatically before git push)
pre-commit install --hook-type pre-push
```

### 3. Run Manually

You can test all files manually at any time:

```bash
pre-commit run --all-files
```

---

## 📖 Javadoc & GitHub Pages

Generated documentation is located in the `docs/` folder, ready for GitHub Pages publishing:

- **Landing Page**: [docs/index.html](docs/index.html)
- **API Javadocs**: [docs/apidocs/index.html](docs/apidocs/index.html)

To regenerate Javadocs locally:

```bash
mvn javadoc:javadoc
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
