# ThreadLocal WebDriver: How to Use

## What is ThreadLocal?

ThreadLocal stores a separate copy of a variable for **each thread**. When you use `parallel="methods"`, each test method runs on a different thread. Without ThreadLocal, they'd share the same WebDriver and break.

**Simple Example:**

```
Thread 1 (Trade_Transactions_Page):  driver instance A
Thread 2 (Trade_Amortisation):       driver instance B  ← Different drivers!
```

---

## Your Updated WebSmokeTest Code

### 1. ThreadLocal Variables (at top of class)

```java
public class WebSmokeTest {
    // Each thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private ThreadLocal<TransactionsPage> transactionsPageThreadLocal = new ThreadLocal<>();
    private ThreadLocal<AmortisationPage> amortisationPageThreadLocal = new ThreadLocal<>();
```

**What this does:**

- `driverThreadLocal` - Stores WebDriver separately for each thread
- `transactionsPageThreadLocal` - Stores TransactionsPage separately for each thread
- `amortisationPageThreadLocal` - Stores AmortisationPage separately for each thread

---

### 2. Getter Methods (to retrieve ThreadLocal values)

```java
    private WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    private void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    private TransactionsPage getTransactionsPage() {
        return transactionsPageThreadLocal.get();
    }

    private void setTransactionsPage(TransactionsPage page) {
        transactionsPageThreadLocal.set(page);
    }

    private AmortisationPage getAmortisationPage() {
        return amortisationPageThreadLocal.get();
    }

    private void setAmortisationPage(AmortisationPage page) {
        amortisationPageThreadLocal.set(page);
    }
```

**What this does:**

- `.get()` - Retrieve the value stored for THIS thread
- `.set()` - Store a value for THIS thread

---

### 3. In @BeforeMethod (store in ThreadLocal)

```java
    @BeforeMethod(alwaysRun = true)
    void Setup() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        // Create a NEW WebDriver for this thread
        WebDriver driver = new ChromeDriver(options);
        setDriver(driver);  // ← STORE in ThreadLocal for this thread

        // Initialize pages with driver
        setTransactionsPage(new TransactionsPage(driver));
        setAmortisationPage(new AmortisationPage(driver));

        driver.manage().window().maximize();

        driver.get("https://development.credebt.com/");

        WebElement emailInput = new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.EMAIL_INPUT));
        emailInput.click();
        emailInput.sendKeys("testuser@credebt.com");
        driver.findElement(Locators.SUBMIT_BUTTON).click();

        WebElement passwordInput = new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.PASSWORD_INPUT));
        passwordInput.click();
        passwordInput.sendKeys("harder@1212");
        driver.findElement(Locators.SUBMIT_BUTTON).click();

        new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.CREDEBTLOGO));
    }
```

**Key changes:**

- Create `WebDriver driver = new ChromeDriver(options)` (local variable)
- Call `setDriver(driver)` to store in ThreadLocal
- Call `setTransactionsPage()` and `setAmortisationPage()` to store page objects

---

### 4. In @Test Methods (retrieve from ThreadLocal)

```java
    @Test(priority = 2)
    public void Trade_Transactions_Page() throws Exception {
        // Retrieve from ThreadLocal
        getTransactionsPage().runTransactionTest();
    }

    @Test(priority = 2)
    public void Trade_Amortisation() throws Exception {
        // Retrieve from ThreadLocal
        getAmortisationPage().runAmortisationTest();
    }
```

**Key changes:**

- Use `getTransactionsPage()` instead of `transactionsPage`
- Use `getAmortisationPage()` instead of `amortisationPage`
- This gets the copy stored for THIS thread

---

### 5. In @AfterMethod (retrieve and cleanup)

```java
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = getDriver();  // ← Retrieve from ThreadLocal
        if (driver != null) {
            driver.manage().deleteAllCookies();
            driver.quit();
            driverThreadLocal.remove();  // ← IMPORTANT: Clean up!
        }
    }
```

**Key changes:**

- Call `getDriver()` to retrieve the WebDriver for THIS thread
- Call `driverThreadLocal.remove()` to clean up after test finishes
- This prevents memory leaks

---

## How It Works With Parallel="methods"

### Execution Timeline

```
Time 0s:  Thread 1                      Thread 2
          @BeforeMethod                 @BeforeMethod
          setDriver(driver1)            setDriver(driver2)
          setTransactionsPage(page1)    setAmortisationPage(page2)

Time 2s:  @Test Trade_Transactions     @Test Trade_Amortisation
          getTransactionsPage()         getAmortisationPage()
          ├─ Returns page1              ├─ Returns page2
          └─ Uses driver1               └─ Uses driver2
             (from Thread 1)               (from Thread 2)

Time 8s:  @AfterMethod                 @AfterMethod
          getDriver() → driver1         getDriver() → driver2
          driver1.quit()                driver2.quit()
          driverThreadLocal.remove()    driverThreadLocal.remove()
```

**Result: 2 completely separate browser instances!**

---

## Usage Examples

### Example 1: Inside your test method

```java
@Test(priority = 2)
public void Trade_Transactions_Page() throws Exception {
    // If you need the driver directly:
    WebDriver driver = getDriver();
    driver.findElement(By.id("some-id")).click();

    // Or use your page object:
    getTransactionsPage().runTransactionTest();
}
```

### Example 2: If you create a custom method

```java
private void navigateToTransactionsPage() {
    WebDriver driver = getDriver();  // Get this thread's driver
    driver.navigate().to("https://development.credebt.com/transactions");
}

@Test(priority = 2)
public void Trade_Transactions_Page() throws Exception {
    navigateToTransactionsPage();
    // Do something with it
}
```

### Example 3: Adding logging with driver info

```java
@BeforeMethod(alwaysRun = true)
void Setup() throws InterruptedException {
    WebDriver driver = new ChromeDriver(options);
    setDriver(driver);

    // Log which thread and driver
    System.out.println("Thread " + Thread.currentThread().getId() +
                       " using driver: " + driver.hashCode());
}
```

---

## testng.xml Configuration

Must have `parallel="methods"` and `thread-count`:

```xml
<suite name="DB-UI Validation Suite" parallel="methods" thread-count="5">
    <test name="Frontend">
        <classes>
            <class name="Web.WebSmokeTest"/>
        </classes>
    </test>
</suite>
```

**Settings:**

- `parallel="methods"` - Run @Test methods in parallel
- `thread-count="5"` - Allow up to 5 threads (adjust based on your system)

---

## Important Rules

### ✅ DO:

1. **Always store in ThreadLocal before using**

```java
WebDriver driver = new ChromeDriver(options);
setDriver(driver);  // ✓ Correct
```

2. **Always retrieve from ThreadLocal before using**

```java
WebDriver driver = getDriver();  // ✓ Correct
driver.findElement(By.id("test")).click();
```

3. **Always clean up after test**

```java
@AfterMethod
public void tearDown() {
    WebDriver driver = getDriver();
    if (driver != null) {
        driver.quit();
        driverThreadLocal.remove();  // ✓ Correct
    }
}
```

### ❌ DON'T:

1. **Don't use instance variables for driver**

```java
private WebDriver driver;  // ❌ Wrong (shared across threads)
driver = new ChromeDriver();  // ❌ Wrong
```

2. **Don't forget to clean up**

```java
driverThreadLocal.remove();  // ❌ Missing this causes memory leaks
```

3. **Don't mix ThreadLocal and instance variables**

```java
private WebDriver driver;              // ❌ Wrong
private ThreadLocal<WebDriver> driverThreadLocal;  // ✓ Right
// Don't use both!
```

---

## Testing Your Setup

### Run with parallel execution:

```bash
mvn clean test
```

**You should see:**

- 2+ browser windows open simultaneously
- Both run at the same time (not one after another)
- No driver conflicts or errors
- Execution time ~50% of sequential

### Run with only one test:

```bash
mvn clean test -Dtest=WebSmokeTest#Trade_Transactions_Page
```

### Run with specific thread count:

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

---

## Common Issues & Solutions

### Issue: "NullPointerException: Cannot get/call method on null"

**Problem:** ThreadLocal value not set

```java
getDriver().findElement(By.id("test"));  // ← Fails if getDriver() returns null
```

**Solution:** Make sure @BeforeMethod runs before @Test

```java
@BeforeMethod(alwaysRun = true)  // ← Must have this
void Setup() throws InterruptedException {
    setDriver(new ChromeDriver(options));  // ← Must set before test runs
}
```

### Issue: "Driver is not responding" or "Stale element"

**Problem:** Using driver from wrong thread

```java
private WebDriver driver;  // ❌ Instance variable (wrong!)
```

**Solution:** Use ThreadLocal

```java
private ThreadLocal<WebDriver> driverThreadLocal;  // ✓ ThreadLocal (right!)
```

### Issue: Memory leak after tests

**Problem:** ThreadLocal not cleaned up

```java
@AfterMethod
public void tearDown() {
    driver.quit();
    // ❌ Missing driverThreadLocal.remove();
}
```

**Solution:** Always remove

```java
@AfterMethod
public void tearDown() {
    WebDriver driver = getDriver();
    driver.quit();
    driverThreadLocal.remove();  // ✓ Clean up!
}
```

---

## Summary

**What ThreadLocal does for you:**

- Stores a separate WebDriver for each thread
- Prevents drivers from interfering with each other
- Allows true parallel test execution
- Enables 2x+ faster test runs

**Your setup now:**

- ✅ Trade_Transactions_Page uses driver instance A
- ✅ Trade_Amortisation uses driver instance B
- ✅ Both run at the same time
- ✅ No conflicts or crashes
