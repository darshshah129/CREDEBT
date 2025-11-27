package org.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGAllureListener implements ITestListener {

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
