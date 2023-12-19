package io.github.renegrob.quarkus.proxy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class IntegerRangeParser {

    private static final Pattern INT_RANGE_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");

    public static final IntegerRangeParser INSTANCE = new IntegerRangeParser();

    private IntegerRangeParser() {
    }

    public int[] parseRange(String statusCodeExpression) {
        String[] listItems = statusCodeExpression.trim().split("\\s*,\\s*");
        IntStream.Builder builder = IntStream.builder();
        for (String item : listItems) {
            Matcher matcher = INT_RANGE_PATTERN.matcher(item);
            if (matcher.matches()) {
                int start = Integer.parseInt(matcher.group(1));
                int end = Integer.parseInt(matcher.group(2));
                if (start > end) {
                    throw new IllegalArgumentException("Invalid expression: start of range must be smaller than end of range.");
                }
                for (int i = start; i <= end; i++) {
                    builder.add(i);
                }
            } else {
                builder.add(Integer.parseInt(item));
            }
        }
        return builder.build().toArray();
    }

}
