package com.github.brittlefoot.treeumph.script;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.function.Function;


public interface NamingStyle extends Function<String[], String> {

    static String camelCaseFrom(String... words) {
        return StringUtils.arrayToDelimitedString(
                Arrays.stream(words)
                        .map(s -> StringUtils.capitalize(s.toLowerCase()))
                        .toArray(),
                "");
    }

    static String lowerCamelCaseFrom(String... words) {
        return StringUtils.uncapitalize(camelCaseFrom(words));
    }

    static String lowerCaseWithUnderscoresFrom(String... words) {
        return StringUtils.arrayToDelimitedString(
                Arrays.stream(words)
                        .map(String::toLowerCase)
                        .toArray(),
                "_");
    }

    NamingStyle CAMEL_CASE = NamingStyle::camelCaseFrom;
    NamingStyle LOWER_CAMEL_CASE = NamingStyle::lowerCamelCaseFrom;
    NamingStyle LOWER_CASE_WITH_UNDERSCORES = NamingStyle::lowerCaseWithUnderscoresFrom;
}
