package com.github.brittlefoot.treeumph.process.util;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ExceptionUtils {

    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

}
