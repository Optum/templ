package com.optum.templ;

import com.optum.templ.exceptions.MissingKeyTemplException;
import com.optum.templ.exceptions.TemplException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplEngine {
    private static final String EMPTY_STRING = "";
    private static final String TO_UPPER_COMMAND_PREFIX = "^";
    private static final String TO_LOWER_COMMAND_PREFIX = "~";

    private static final String TEMPLATE_CANARY = "{{";
    /**
     * The { character must be escaped outside the [] block.  Inside it does not.
     * The } character does not need to be escaped.
     */
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^{}]*)}}");

    /**
     * The eleven prefix/suffix delimiters that are currently supported are: (unescaped, and listed in the order listed in the code)
     *   <space> , . ; : ? / ( ) _ - \
     *
     * Reminder: You need to escape some characters for Regex with a backslash prefix,
     * and all backslashes prefixed with another backslash for Java.
     *   - needs to be Regex escaped with a backslash.  Java has to escape the backslash.  So it becomes \\-
     *   \ needs to be Regex escaped with a backslash.  Java has to escape BOTH backslashes.  So it becomes \\\\
     */
    private static final String DELIMITERS = "[ ,.;:?&@#/()<>_\\-\\\\|]";
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^(" + DELIMITERS + "*)([\\^~]?)");
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("(" + DELIMITERS + "+)$");

    final TemplDataSource dataSource;
    final boolean allowMissingKeys;

    public TemplEngine(TemplDataSource dataSource) {
        this(dataSource, false);
    }

    public TemplEngine(TemplDataSource dataSource, boolean allowMissingKeys) {
        this.dataSource = dataSource;
        this.allowMissingKeys = allowMissingKeys;
    }

    public String processTemplate(String rawValue) throws TemplException {
        if (rawValue==null)
            return null;

        // Quick reject test
        if (!rawValue.contains(TEMPLATE_CANARY))
            return rawValue;

        String intermediateValue = rawValue;
        boolean more = true;
        while(more) {
            more = false;
            Matcher matcher = TEMPLATE_PATTERN.matcher(intermediateValue);

            while (matcher.find()) {
                more = true;
                String templateVar = matcher.group(1);
                int start = matcher.start();
                int end = matcher.end();
                String replacement = resolveTemplateFragment(templateVar);
                intermediateValue = intermediateValue.substring(0, start) + replacement + intermediateValue.substring(end);
                matcher = TEMPLATE_PATTERN.matcher(intermediateValue);
            }
        }

        return intermediateValue;
    }

    private String resolveTemplateFragment(String template) throws TemplException {
        final String prefix;
        final boolean uppercase;
        final boolean lowercase;
        Matcher prefixMatcher = PREFIX_PATTERN.matcher(template);
        if (prefixMatcher.find())  {
            prefix = prefixMatcher.group(1);
            uppercase = TO_UPPER_COMMAND_PREFIX.equals(prefixMatcher.group(2));
            lowercase = TO_LOWER_COMMAND_PREFIX.equals(prefixMatcher.group(2));
            template = template.substring(prefix.length()+((uppercase||lowercase)?1:0));
        } else {
            prefix = EMPTY_STRING;
            uppercase = false;
            lowercase = false;
        }

        final String suffix;
        Matcher suffixMatcher = SUFFIX_PATTERN.matcher(template);
        if (suffixMatcher.find()) {
            suffix = suffixMatcher.group(1);
            template = template.substring(0, template.length()-suffix.length());
        } else {
            suffix = EMPTY_STRING;
        }

        final String result = dataSource.lookupValueForKey(template);
        if (result==null) {
            if (allowMissingKeys) {
                return EMPTY_STRING;
            } else {
                throw new MissingKeyTemplException(template);
            }
        } else if (!result.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            if (uppercase) {
                sb.append(result.toUpperCase().replace("-","_"));
            } else if (lowercase) {
                sb.append(result.toLowerCase().replace("_","-"));
            } else {
                sb.append(result);
            }
            sb.append(suffix);
            return sb.toString();
        } else {
            return EMPTY_STRING;
        }
    }

}
