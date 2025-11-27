package org.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestNGAllureListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // Warm up the classloader by forcing core classes to load early
        // This prevents ClassNotFoundException race conditions when parallel tests try to load classes simultaneously
        try {
            Class.forName("org.utils.Locators");
            Class.forName("org.pages.TransactionsPage");
            Class.forName("org.pages.AmortisationPage");
            Common.log("✓ Classloader warmup: core classes loaded successfully");
        } catch (ClassNotFoundException e) {
            Common.error("Failed to warm up classloader: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        // no-op
    }

    @Override
    public void onTestStart(ITestResult result) {
        // nothing to do here; buffer initializes on first log
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Common.attachAndClearTestLog(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Common.attachAndClearTestLog(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Common.attachAndClearTestLog(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        Common.attachAndClearTestLog(result);
    }

    @Override
    public void onStart(ITestContext context) {
        // no-op
    }

    @Override
    public void onFinish(ITestContext context) {
        // no-op
    }
}
