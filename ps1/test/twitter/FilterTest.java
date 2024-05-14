/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
   /*
     * Testing strategy for writtenBy:
     * - No tweets
     * - One tweet, author matches
     * - One tweet, author does not match
     * - Multiple tweets, some authors match
     * - Multiple tweets, no authors match
     * - Case sensitivity of author names
     * 
     * Testing strategy for inTimespan:
     * - No tweets
     * - One tweet inside timespan
     * - One tweet outside timespan
     * - Multiple tweets, some inside timespan
     * - Multiple tweets, all inside timespan
     * - Multiple tweets, none inside timespan
     * 
     * Testing strategy for containing:
     * - No tweets
     * - One tweet, contains word
     * - One tweet, does not contain word
     * - Multiple tweets, some contain words
     * - Multiple tweets, none contain words
     * - Words are case-insensitive
     * - Words appear in different parts of the tweet (start, middle, end)
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "Let's talk about coding", d3);
    private static final Tweet tweet4 = new Tweet(4, "alyssa", "Nothing much to say here", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByNoTweets() {
        List<Tweet> writtenBy = Filter.writtenBy(Collections.emptyList(), "alyssa");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByOneTweetAuthorMatches() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testWrittenByOneTweetAuthorDoesNotMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "bbitdiddle");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByMultipleTweetsSomeAuthorsMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet4), "alyssa");
        
        assertEquals("expected 2 tweets", 2, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
        assertTrue("expected list to contain tweet4", writtenBy.contains(tweet4));
    }
    
    @Test
    public void testWrittenByMultipleTweetsNoAuthorsMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "charlie");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByCaseSensitivity() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "Alyssa");
        
        assertTrue("expected empty list due to case sensitivity", writtenBy.isEmpty());
    }
    
    @Test
    public void testInTimespanNoTweets() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Collections.emptyList(), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanOneTweetInside() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton list", 1, inTimespan.size());
        assertTrue("expected list to contain tweet", inTimespan.contains(tweet1));
    }
    
    @Test
    public void testInTimespanOneTweetOutside() {
        Instant testStart = Instant.parse("2016-02-17T11:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanMultipleTweetsSomeInside() {
        Instant testStart = Instant.parse("2016-02-17T10:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton list", 1, inTimespan.size());
        assertTrue("expected list to contain tweet", inTimespan.contains(tweet2));
    }
    
    @Test
    public void testInTimespanMultipleTweetsAllInside() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected 2 tweets", 2, inTimespan.size());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
    }
    
    @Test
    public void testInTimespanMultipleTweetsNoneInside() {
        Instant testStart = Instant.parse("2016-02-17T12:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T13:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testContainingNoTweets() {
        List<Tweet> containing = Filter.containing(Collections.emptyList(), Arrays.asList("talk"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingOneTweetContainsWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk"));
        
        assertEquals("expected singleton list", 1, containing.size());
        assertTrue("expected list to contain tweet", containing.contains(tweet1));
    }
    
    @Test
    public void testContainingOneTweetDoesNotContainWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("coding"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingMultipleTweetsSomeContainWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk"));
        
        assertEquals("expected 3 tweets", 3, containing.size());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }
    
    @Test
    public void testContainingMultipleTweetsNoneContainWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("coding"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingWordsCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("TALK"));
        
        assertEquals("expected 2 tweets", 2, containing.size());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }
    
    @Test
    public void testContainingWordsAppearInDifferentParts() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("reasonable", "rivest", "coding"));
        
        assertEquals("expected 3 tweets", 3, containing.size());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
