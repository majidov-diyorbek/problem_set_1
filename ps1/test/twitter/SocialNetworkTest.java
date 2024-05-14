/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
      /*
     * Testing strategy for guessFollowsGraph:
     * - No tweets
     * - One tweet, no mentions
     * - One tweet, one mention
     * - Multiple tweets, no mentions
     * - Multiple tweets, multiple mentions
     * - Multiple tweets, some with mentions
     * - Mentions in different cases (case-insensitivity)
     * - Multiple mentions in a single tweet
     * 
     * Testing strategy for influencers:
     * - Empty graph
     * - One user following no one
     * - One user followed by others
     * - Multiple users, some follow each other
     * - Multiple users, complex follow graph
     */
    
    private static final Instant d1 = Instant.parse("2023-05-14T10:00:00Z");
    private static final Instant d2 = Instant.parse("2023-05-14T11:00:00Z");
    private static final Instant d3 = Instant.parse("2023-05-14T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alice", "I love programming!", d1);
    private static final Tweet tweet2 = new Tweet(2, "bob", "Programming is awesome @alice", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "Check this out @bob @alice", d3);
    private static final Tweet tweet4 = new Tweet(4, "dave", "Hello world!", d3);
    private static final Tweet tweet5 = new Tweet(5, "eve", "@Charlie you are amazing!", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Collections.emptyList());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphOneTweetNoMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphOneTweetOneMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        
        assertEquals("expected 1 follower", 1, followsGraph.size());
        assertTrue("expected bob to follow alice", followsGraph.get("bob").contains("alice"));
    }
    
    @Test
    public void testGuessFollowsGraphMultipleTweetsNoMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet4));
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphMultipleTweetsMultipleMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet3, tweet5));
        
        assertEquals("expected 3 followers", 3, followsGraph.size());
        assertTrue("expected bob to follow alice", followsGraph.get("bob").contains("alice"));
        assertTrue("expected charlie to follow bob and alice", followsGraph.get("charlie").containsAll(Arrays.asList("bob", "alice")));
        assertTrue("expected eve to follow charlie", followsGraph.get("eve").contains("charlie"));
    }
    
    @Test
    public void testGuessFollowsGraphCaseInsensitivity() {
        Tweet tweet6 = new Tweet(6, "frank", "Hi @Alice @BOB", d3);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet6));
        
        assertEquals("expected 1 follower", 1, followsGraph.size());
        assertTrue("expected frank to follow alice and bob", followsGraph.get("frank").containsAll(Arrays.asList("alice", "bob")));
    }
    
    @Test
    public void testGuessFollowsGraphMultipleMentionsInOneTweet() {
        Tweet tweet7 = new Tweet(7, "george", "Hey @alice @bob @charlie", d3);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7));
        
        assertEquals("expected 1 follower", 1, followsGraph.size());
        assertTrue("expected george to follow alice, bob, and charlie", followsGraph.get("george").containsAll(Arrays.asList("alice", "bob", "charlie")));
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    @Test
    public void testInfluencersOneUserFollowsNoOne() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    @Test
    public void testInfluencersOneUserFollowedByOthers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", new HashSet<>(Arrays.asList("alice")));
        followsGraph.put("charlie", new HashSet<>(Arrays.asList("alice")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 1 influencer", 1, influencers.size());
        assertEquals("expected alice as top influencer", "alice", influencers.get(0));
    }
    
    @Test
    public void testInfluencersMultipleUsersSomeFollowEachOther() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", new HashSet<>(Arrays.asList("alice", "charlie")));
        followsGraph.put("charlie", new HashSet<>(Arrays.asList("alice")));
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 3 influencers", 3, influencers.size());
        assertTrue("expected alice as top influencer", influencers.get(0).equals("alice"));
    }
    
    @Test
    public void testInfluencersComplexGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bob", new HashSet<>(Arrays.asList("alice", "charlie", "dave")));
        followsGraph.put("charlie", new HashSet<>(Arrays.asList("alice")));
        followsGraph.put("dave", new HashSet<>(Arrays.asList("alice")));
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 4 influencers", 4, influencers.size());
        assertEquals("expected alice as top influencer", "alice", influencers.get(0));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
