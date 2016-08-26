package edu.gatech.dynodroid.android;

import java.util.regex.Matcher;

/**
 * @author Mygod
 */
public class AuthorityEntry {
    public String host;
    public int port;
    public boolean wild;

    public AuthorityEntry(String host, int port, boolean wild) {
        this.host = host;
        this.port = port;
        this.wild = wild;
    }
    public AuthorityEntry(Matcher matcher) {
        host = matcher.group(1);
        String p = matcher.group(2);
        port = p == null ? -1 : Integer.parseInt(p);
        wild = matcher.group(3) != null;
    }
}
