# CREDEBT - Test Refactoring Guide

## Overview

The test code has been refactored to follow a **Page Object Model (POM)** with **Service Layer** architecture. This separates test logic from page interactions and makes tests more maintainable and reusable.

## Directory Structure

```
src/
├── main/java/org/
│   ├── pages/              # Page Object classes
│   │   ├── TransactionsPage.java
│   │   └── AmortisationPage.java
│   ├── services/           # Service layer for test orchestration
│   │   ├── TransactionsTestService.java
│   │   └── AmortisationTestService.java
│   └── utils/              # Shared utilities
│       ├── Common.java     # Common methods (logging, waits, assertions)
│       └── Locators.java   # Centralized XPath locators
│
└── test/java/
    ├── Web/
    │   └── WebSmokeTest.java  # Main test class (now simplified)
    ├── DB/
    │   └── DatabaseSmokeTest.java
    ├── Compare/
    │   └── Compare.java
    └── utils/                # Legacy utilities (kept for backward compatibility)
        ├── Common.java       # Original - still used by DB/Compare tests
        ├── Locators.java
        └── ... other utilities
```

## Architecture Layers

### 1. **Page Objects** (`src/main/java/org/pages/`)

Page classes represent UI pages/features and contain methods for interactions:

- **`TransactionsPage`**: Handles all transaction page interactions

  - `navigateToTransactions()` - Navigate to transactions page
  - `getTransactionCountByType(String type)` - Get count for specific type
  - `extractAllTransactionCounts()` - Extract all transaction types
  - `testAggregateTransactionFunctions()` - Test aggregate functions

- **`AmortisationPage`**: Handles all amortisation page interactions
  - `navigateToAmortisation()` - Navigate to amortisation page
  - `getAmortisationCountByType(String type)` - Get amortisation count
  - `extractAllAmortisationCounts()` - Extract all amortisation types

### 2. **Services** (`src/main/java/org/services/`)

Service classes orchestrate the test flow by calling page methods:

- **`TransactionsTestService`**: Coordinates transaction test workflow

  - `executeTransactionTests()` - Runs complete transaction test flow

- **`AmortisationTestService`**: Coordinates amortisation test workflow
  - `executeAmortisationTests()` - Runs complete amortisation test flow

### 3. **Test Classes** (`src/test/java/Web/`)

Test classes are now simplified and delegate to services:

```java
@Test
public void Trade_Transactions_Page() throws Exception {
    transactionsTestService.executeTransactionTests();
}
```

### 4. **Utilities** (`src/main/java/org/utils/`)

Shared utilities moved to main source:

- **`Common.java`**: Test helper methods
- **`Locators.java`**: Centralized XPath locators

## Benefits of This Architecture

✅ **Separation of Concerns**: Page logic, test logic, and utilities are separated  
✅ **Reusability**: Page methods can be reused across multiple test classes  
✅ **Maintainability**: Changes to UI interaction logic are isolated to Page classes  
✅ **Readability**: Test methods are concise and read like test scenarios  
✅ **Scalability**: Easy to add new page objects and test services

## How to Add a New Test

To add a new test feature (e.g., "Closing Positions"):

### Step 1: Create Page Object

Create `src/main/java/org/pages/ClosingPositionsPage.java`:

```java
package org.pages;

import org.openqa.selenium.WebDriver;
import org.utils.Common;
import org.utils.Locators;

public class ClosingPositionsPage {
    private WebDriver driver;

    public ClosingPositionsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToClosingPositions() throws Exception {
        Common.clickElement(driver, Locators.CLOSINGPOSITIONSBUTTON);
        Thread.sleep(5000);
    }

    // Add more methods for page interactions...
}
```

### Step 2: Create Test Service

Create `src/main/java/org/services/ClosingPositionsTestService.java`:

```java
package org.services;

import org.openqa.selenium.WebDriver;
import org.pages.ClosingPositionsPage;

public class ClosingPositionsTestService {
    private ClosingPositionsPage closingPage;

    public ClosingPositionsTestService(WebDriver driver) {
        this.closingPage = new ClosingPositionsPage(driver);
    }

    public void executeClosingPositionTests() throws Exception {
        closingPage.navigateToClosingPositions();
        // Call page methods to perform test steps...
    }
}
```

### Step 3: Add Test Method

Add to `src/test/java/Web/WebSmokeTest.java`:

```java
private ClosingPositionsTestService closingPositionsTestService;

@BeforeMethod
void Setup() throws InterruptedException {
    // ... existing setup code ...
    closingPositionsTestService = new ClosingPositionsTestService(driver);
}

@Test
public void Trade_Closing_Positions() throws Exception {
    closingPositionsTestService.executeClosingPositionTests();
}
```

## Running Tests

### Run all UI tests

```bash
mvn clean test -Dtest=WebSmokeTest
```

### Run all tests (DB → UI → Compare)

```bash
mvn clean test
```

### Run with parallel execution (5 threads)

```bash
mvn clean test -DparallelForkCount=5
```

### View Allure reports

```bash
mvn allure:serve
```

## Important Notes

⚠️ **Backward Compatibility**: The original `utils/` in `src/test/java/` is retained for DB and Compare tests. Both `utils` packages now exist:

- `utils.Common` / `utils.Locators` (in test folder - used by DB/Compare)
- `org.utils.Common` / `org.utils.Locators` (in main folder - used by Web tests)

✅ **Data Flow**: Test data still flows through the shared JSON file:

1. DB tests → `StoredData/db_values.json`
2. Web tests → `StoredData/FE_Values.json`
3. Compare → Validates both JSON files match

## File Locations Reference

| Component        | Location                      |
| ---------------- | ----------------------------- |
| Page Objects     | `src/main/java/org/pages/`    |
| Test Services    | `src/main/java/org/services/` |
| Main Utilities   | `src/main/java/org/utils/`    |
| Test Classes     | `src/test/java/Web/`          |
| Legacy Utilities | `src/test/java/utils/`        |
| Test Config      | `testng.xml`                  |
| Build Config     | `pom.xml`                     |
