# **Planning Document Review**

## **1.Game Functions**
* All the functions for Matchmaking, Ranking and Leaderboard looks great, very well planned and systematic to implement. 
* However, here are few suggestions for improvement:
    * findPlayer(): Instead of looping continuously, implement a priority queue where players are sorted by ELO. This makes potential matches efficient.
    * setCheckersPositions(): Instead of iterating over the entire list, use binary search (O(log n)) to find the player's old position and insert them into the correct spot. Also, your approach (placing tied players at the bottom of the group) is good, but consider breaking ties using number of games played.
    
## **2.Phases**
* The effort to reduce matchmaking time by introducing AI bots in Phase 3 is a smart strategy, ensuring players are not stuck in long queues.
* However, there is an inconsistency in the ranking system:
  * Phase 2 planned for three ranks (Bronze, Silver, Gold).
  * The "Getting a Rank" section later mentions five ranks (Bronze, Silver, Gold, Platinum, Diamond).
  * This contradiction should be resolved, decide on a final structure and ensure uniformity across all sections.

## **3.Explanation of groups portion of class diagram**
* Your current approach (stopping search as soon as a valid player is found) works well, but instead go for a short wait time to allow better matches.
* Your plan for sorting when a player clicks the leaderboard button is good. If real-time updates are needed, use lazy sorting (only sort if changes occur instead of every access).

## **4.Feedback**
* Planning lacks sufficient detail about timelines and progress. This makes it difficult to determine if proper deadlines exist or are being met.
* Overall planning and details were up to standard.