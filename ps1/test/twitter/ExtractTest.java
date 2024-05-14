/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */

      /*
     * Testing strategy for getTimespan:
     * - No tweets
     * - One tweet
     * - Two tweets
     * - More than two tweets
     *
     * Testing strategy for getMentionedUsers:
     * - No tweets
     * - One tweet, no mentions
     * - One tweet, one mention at the beginning
     * - One tweet, one mention in the middle
     * - One tweet, one mention at the end
     * - One tweet, multiple mentions
     * - Multiple tweets, with and without mentions
     * - Mentions with mixed case usernames
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "@alice check this out!", d3);
    private static final Tweet tweet4 = new Tweet(4, "david", "Hey @bob, are you going to the event?", d3);
    private static final Tweet tweet5 = new Tweet(5, "eve", "No mentions here.", d3);
    private static final Tweet tweet6 = new Tweet(6, "frank", "Mentions @alice and @Bob again!", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanNoTweets() {
        Timespan timespan = Extract.getTimespan(Collections.emptyList());
        
        assertEquals("expected start to be end", timespan.getStart(), timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("expected start and end to be the same", d1, timespan.getStart());
        assertEquals("expected start and end to be the same", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMoreThanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoTweets() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.emptyList());
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersOneMentionAtBeginning() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        
        assertEquals("expected one user mentioned", 1, mentionedUsers.size());
        assertTrue("expected alice to be mentioned", mentionedUsers.contains("alice"));
    }
    
    @Test
    public void testGetMentionedUsersOneMentionInMiddle() {
        Tweet tweet = new Tweet(7, "greg", "Meeting @hank for lunch.", d3);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        
        assertEquals("expected one user mentioned", 1, mentionedUsers.size());
        assertTrue("expected hank to be mentioned", mentionedUsers.contains("hank"));
    }

    @Test
    public void testGetMentionedUsersOneMentionAtEnd() {
        Tweet tweet = new Tweet(8, "ian", "Lunch meeting with @jake", d3);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        
        assertEquals("expected one user mentioned", 1, mentionedUsers.size());
        assertTrue("expected jake to be mentioned", mentionedUsers.contains("jake"));
    }
    
    @Test
    public void testGetMentionedUsersMultipleMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet6));
        
        assertEquals("expected two users mentioned", 2, mentionedUsers.size());
        assertTrue("expected alice to be mentioned", mentionedUsers.contains("alice"));
        assertTrue("expected bob to be mentioned", mentionedUsers.contains("bob"));
    }

    @Test
    public void testGetMentionedUsersMultipleTweets() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4, tweet5));
        
        assertEquals("expected one user mentioned", 1, mentionedUsers.size());
        assertTrue("expected bob to be mentioned", mentionedUsers.contains("bob"));
    }
    
    @Test
    public void testGetMentionedUsersMixedCaseUsernames() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4, tweet6));
        
        assertEquals("expected two users mentioned", 2, mentionedUsers.size());
        assertTrue("expected alice to be mentioned", mentionedUsers.contains("alice"));
        assertTrue("expected bob to be mentioned", mentionedUsers.contains("bob"));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
