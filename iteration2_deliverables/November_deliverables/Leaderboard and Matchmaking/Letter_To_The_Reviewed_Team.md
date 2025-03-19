# ELO Ranking General Critique

This ranking system works well for 1v1 board games like chess and checkers. It fairly adjusts player ratings based on match results, making sure
stronger players move up while weaker players adjust. It is simple, effective, and a good choice for ranking competitive 1v1 games. However, 
for Tic-Tac-Toe the ranking adjustments can be ineffective as:
- Tic-Tac-Toe is a solved game, meaning that at higher ranks, most matches will end in a draw. This makes the ranking system ineffective, as 
  players will rarely gain or lose points. 
- Since new players may have a higher K-factor, they could gain an unfair advantage when drawing against experienced players, even though 
  both played optimally. 

# Use Case Descriptions & Diagram

## General Critique
The use case descriptions cover various features, particularly those related to matchmaking with friends. However, incorporating personal 
statistics into matchmaking and leaderboard functionalities reduces cohesion. A matchmaking and leaderboard system should remain focused on its 
primary functions, while personal stats would be better handled by the profile and authentication system. 
Additionally, all use cases were assigned to the third iteration, despite differing priorities. This approach may not allow sufficient time for
proper testing and integration. It would be beneficial to align iteration planning with feature priority, ensuring that core system 
implementations are developed and tested earlier in the process.

## Feature Requests
**Additional Features**
- Introduce seasonal leaderboards
- Implement a matchmaking history feature

## Constructive Feedback
Some use case triggers were unclear, appearing more like descriptions. Clarifying these triggers would improve readability and usability. 
Additionally, certain core matchmaking functionalities were listed as open issues rather than being fully defined. Providing more detailed 
definitions for these aspects would enhance clarity.
Regarding the use case diagram, the inclusion of a lobby after matchmaking may not be necessary for two-player games. The matchmaking system 
should pair players, making them ready to start the game without an additional lobby step. Finally, ensuring consistency between the use case
descriptions and the diagram would improve coherence and accuracy.

# Class Structure Diagram
## General Critique
The Leaderboard & Matchmaking system has a well-structured design that properly maintains player statistics, ranks, 
and pairing procedures while maintaining competitive balance.  The inclusion of crucial linkages between player data, matchmaking logic, 
and rankings is advantageous.  However, the system is unclear about how ranks are calculated—whether using Elo, MMR, 
or another algorithm—and does not specify how frequently the leaderboard refreshes. 
Another area for improvement is managing edge circumstances, such as disconnected players or abandoned matches, in order to provide a consistent player experience.  
Overall, the design is strong, however some features might be improved for more robustness and clarity.

## Feature Requests
- Introduce tier-based leaderboards (e.g., Bronze, Silver, Gold, and so on) and seasonal ranking resets to encourage ongoing player participation.
- Implement a dynamic skill-based matchmaking system that evolves according to recent player performance trends.

## Constructive Feedback
The Leaderboard & Matchmaking system requires improved readability, including clearer linkages and uniform naming conventions.UML 
relationships, particularly those between Leaderboard and PlayerStatistics, should explicitly specify aggregate or composition.The
matchmaking mechanism is tightly connected, and employing a strategy pattern would provide flexibility for various ranking systems such as Elo or MMR.Edge
circumstances, such as player disconnections and abandoned matches, should be handled with timeouts or fines.Optimizing 
leaderboard updates through batch processing or event-driven refreshes will boost performance and efficiency.