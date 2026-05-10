package com.spectrayan.promptly.shared.util;

import java.util.Arrays;

/**
 * Utility class for processing exceptions to avoid verbose reactive stack traces.
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        // Utility class
    }

    /**
     * Extracts the root cause message and the first relevant stack trace element.
     */
    public static String getRootCauseMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error";
        }
        
        Throwable rootCause = getRootCause(throwable);
        String message = rootCause.getMessage() != null ? rootCause.getMessage() : rootCause.getClass().getSimpleName();
        
        // Find the first relevant stack trace element (e.g. from our code base)
        String trace = Arrays.stream(rootCause.getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("com.spectrayan.promptly"))
                .findFirst()
                .map(ste -> String.format(" at %s.%s(%s:%d)", 
                        ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber()))
                .orElse("");
                
        if (trace.isEmpty() && rootCause.getStackTrace().length > 0) {
            // Fallback to top of stack if no promptly classes found
            StackTraceElement top = rootCause.getStackTrace()[0];
            trace = String.format(" at %s.%s(%s:%d)", 
                        top.getClassName(), top.getMethodName(), top.getFileName(), top.getLineNumber());
        }

        return rootCause.getClass().getSimpleName() + ": " + message + trace;
    }

    /**
     * Finds the root cause of an exception.
     */
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }
}
