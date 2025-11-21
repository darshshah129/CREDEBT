package utils;

import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AllureConsoleListener implements ITestListener {

    private ByteArrayOutputStream consoleBuffer;
    private PrintStream originalOut;

    @Override
    public void onTestStart(ITestResult result) {
        consoleBuffer = new ByteArrayOutputStream();
        originalOut = System.out;

        System.setOut(new PrintStream(consoleBuffer));   // Redirect System.out
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        attachLog();
        System.setOut(originalOut);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachLog();
        System.setOut(originalOut);
    }

    @Override
    public void onTestSkipped(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}

    @Attachment(value = "Console Output", type = "text/plain")
    public byte[] attachLog() {
        return consoleBuffer.toByteArray();
    }
}
