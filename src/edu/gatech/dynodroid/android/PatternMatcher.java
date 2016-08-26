package edu.gatech.dynodroid.android;

import java.util.regex.Matcher;

/**
 * @author Mygod
 */
public class PatternMatcher {
    public static final int
            PATTERN_LITERAL = 0,
            PATTERN_PREFIX = 1,
            PATTERN_SIMPLE_GLOB = 2;

    public String path;
    public int type;

    public PatternMatcher(String path, int type) {
        this.path = path;
        this.type = type;
    }
    public PatternMatcher(Matcher matcher) {
        switch (matcher.group(1)) {
            case "LITERAL":
                type = 0;
                break;
            case "PREFIX":
                type = 1;
                break;
            case "GLOB":
                type = 2;
                break;
        }
        path = matcher.group(2);
    }

    @Override
    public int hashCode() {
        return path.hashCode() ^ type;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof PatternMatcher) {
            PatternMatcher matcher = (PatternMatcher) other;
            return type == matcher.type && path.equals(matcher.path);
        }
        return false;
    }
}
