# AI Coding Agent Instructions for CREDEBT

## Project Overview

**CREDEBT** is a **DB-UI Validation Framework** written in Java that performs automated testing by comparing database results with frontend UI results. It uses Selenium for UI automation, TestNG for test orchestration, and Allure for test reporting.

### Architecture: Three-Phase Validation Flow

1. **Database Phase** (`DB/DatabaseSmokeTest.java`): Executes parameterized SQL queries, extracts results into structured JSON
2. **Frontend Phase** (`Web/WebSmokeTest.java`): Navigates web UI, captures displayed values into same JSON structure
3. **Comparison Phase** (`Compare/Compare.java`): Validates DB and UI values match exactly for financial transaction records

**Key Insight**: All three test phases export values to a shared `Map<String, Object>` that gets serialized to `StoredData/db_values.json`. Tests communicate through this file, not direct method calls.

---

## Critical Developer Workflows

### Running Tests Locally

```bash
# Run full test suite (DB → Web → Compare in parallel, configurable via testng.xml)
mvn clean test

# Run only DB validation tests
mvn clean test -Dtest=DatabaseSmokeTest

# Run only UI tests (parallel execution, 5 threads)
mvn clean test -Dtest=WebSmokeTest

# Connect to custom database at runtime
mvn clean test \
  -Ddb.url="jdbc:mysql://your-host:3306/db-name" \
  -Ddb.user="username" \
  -Ddb.pass="password"

# View Allure test reports after run
mvn allure:serve  # opens HTML dashboard
```

### Test Data Flow Pattern

- **DB exports to JSON**: `utils/DBHelper.java` queries database → results stored in `DatabaseSmokeTest.EXPORTED_VALUES` (static Map)
- **Web exports to JSON**: `Common.writeFEJson()` (called in `@AfterClass`) writes UI-captured values to same JSON file
- **Compare phase reads**: `Compare.compare()` calls `common.compareDB_FE("DB_KEY", "FE_KEY")` to assert both keys exist and values match
- **Output location**: `StoredData/db_values.json` (created after each test run)

---

## Project-Specific Patterns & Conventions

### 1. **Locator Management** (`utils/Locators.java`)

- All Selenium locators centralized in static `By` fields
- Naming convention: `FEATURE_ELEMENT_TYPE` (e.g., `TRANSACTIONS_TYPE_DROPDOWN`, `TOTAL_COUNT_TRANSACTION`)
- Uses XPath exclusively (ng-select for Angular dropdowns, normalize-space for whitespace tolerance)
- When adding new locators: follow naming pattern, test in browser console first

### 2. **Common Utility Methods** (`utils/Common.java`)

- **`verifyElementVisible(driver, locator)`**: Waits for element visibility, highlights in green, asserts `isDisplayed()`
- **`clickElement(driver, locator)`**: Waits for clickable, highlights in red, includes retry logic if first click fails
- **`selectNgDropdown(driver, locator, value)`**: Special handler for Angular ng-select dropdowns
- **`waitForLoaderToDisappear(driver)`**: Polls for loading spinner (class: `ng-tns-c2009170884-0`), exits when gone
- **`addFEValue(key, value)`** and **`addDBValue(key, value)`**: Store extracted values in the shared Map for later comparison
- **All logging goes through `Common.log()`**: Auto-increments step counter, attaches to Allure reports

### 3. **Database Query Pattern** (`DB/DatabaseSmokeTest.java`)

Each query wrapped in `QueryInfo` object specifying:

```java
new QueryInfo(
    "SELECT COUNT(*) as count FROM table WHERE condition",
    "DB_GET_A_ETR_RECORDS",        // Export key name
    "count",                        // Column name to extract
    ExtractionType.COUNT_INT       // Type: COUNT_INT, DOUBLE_VALUE, COUNT_ROWS, COUNT_EXISTS
)
```

- **Extraction types**: INT counts, DOUBLE for sums/averages, COUNT_ROWS for row iteration, COUNT_EXISTS for existence checks
- **Connection**: Created once per `@BeforeClass`, managed by `DBHelper` using System properties
- **All extracted values**: Automatically stored in `EXPORTED_VALUES` Map with key from `variableName`

### 4. **Test Class Separation by Concern**

- `Web/`: All UI/Selenium code; extends Locators and Common utilities
- `DB/`: Only database code; no UI dependencies
- `Compare/`: Pure comparison logic; reads from shared JSON
- `utils/`: Shared helpers (Locators, Common, DBHelper, logging)

### 5. **Login Flow** (Hard-Coded in WebSmokeTest Setup)

```java
// Current credentials (development environment)
Email: testuser@credebt.com
Password: harder@1212
// Base URL: https://development.credebt.com/
```

- Located in `@BeforeMethod Setup()` → customize if environment changes
- Credentials should be moved to external config if multiple environments needed

### 6. **Parallel Execution** (`testng.xml`)

- Frontend tests run with `parallel="classes" thread-count="5"` for speed
- Database and Compare tests run sequentially (data dependency)
- Add new test classes to appropriate `<test>` block; don't modify thread count without load testing

---

## Integration Points & Dependencies

### External Services

- **Database**: MySQL (configurable via `-Ddb.url`, defaults to `jdbc:mysql://development.credebt.com:3306/Credebt_Machine`)
- **Web Application**: Angular-based UI at `https://development.credebt.com/` with ng-select components
- **WebDriver**: Chrome (default, incognito mode); Firefox and Edge configs commented out in Setup()

### Dependencies (Key Versions)

- **Selenium 4.15.0**: UI automation; includes WebDriverWait for explicit waits
- **TestNG 7.8.0**: Test framework; parallel execution via suite XML
- **MySQL Connector 8.0.33**: Database connectivity
- **WebDriverManager 5.6.2**: Auto-downloads/manages browser drivers (no manual driver setup needed)
- **Allure 2.30.0**: Test reporting; integrated via Allure Maven plugin

### Cross-Component Communication

1. `DatabaseSmokeTest` extracts values → writes `StoredData/db_values.json`
2. `WebSmokeTest` reads same file location, adds UI values
3. `Compare` reads final JSON → validates all DB/FE keys match

- **Note**: If JSON structure changes, all three classes must be updated (no schema file exists)

---

## Common Pitfalls & Debugging

### Debugging Failures

1. **Test hangs on `verifyElementVisible()` or `clickElement()`**: Element not found or loader spinner stuck
   - Add `waitForLoaderToDisappear()` call before attempting the action
   - Check locator in browser: `$x("//xpath-here")` in console
2. **`compareDB_FE()` fails**: DB key or FE key missing from JSON
   - Verify `addDBValue()` and `addFEValue()` called in respective test classes
   - Check `StoredData/db_values.json` after test run for both keys
3. **DB connection fails**: Check `-Ddb.url`, `-Ddb.user`, `-Ddb.pass` System properties
4. **Parallel tests interfere**: Check `testng.xml` thread-count; increase if race conditions appear

### Adding New Transaction Types

Example: To test "e-ETR" transactions:

1. In `Web/WebSmokeTest.java` → `Trade_Transactions_Page()` test, add:
   ```java
   Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "e-ETR");
   String FE_GET_E_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
   Common.addFEValue("FE_GET_E_ETR_RECORDS", FE_GET_E_ETR_RECORDS);
   ```
2. In `DB/DatabaseSmokeTest.java` → `@Test` method, add query:
   ```java
   executeQuery(new QueryInfo("SELECT COUNT(*) as count FROM transactions WHERE type='e-ETR'",
       "DB_GET_E_ETR_RECORDS", "count", ExtractionType.COUNT_INT));
   ```
3. In `Compare/Compare.java`, add:
   ```java
   common.compareDB_FE("DB_GET_E_ETR_RECORDS", "FE_GET_E_ETR_RECORDS");
   ```

---

## References to Key Code Locations

- **Locators**: `src/test/java/utils/Locators.java` (60+ XPath definitions)
- **Common Utilities**: `src/test/java/utils/Common.java` (382 lines, step logging, waits, highlighting)
- **UI Test Entry Point**: `src/test/java/Web/WebSmokeTest.java` (250 lines)
- **DB Test Entry Point**: `src/test/java/DB/DatabaseSmokeTest.java` (282 lines)
- **Comparison Logic**: `src/test/java/Compare/Compare.java` (22 comparisons)
- **Test Configuration**: `testng.xml` (suite definition, parallel settings)
- **Build Config**: `pom.xml` (dependencies, Allure plugin, Maven Surefire)
