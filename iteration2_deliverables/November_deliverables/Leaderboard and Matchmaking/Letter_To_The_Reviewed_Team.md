# ELO Ranking General Critique

This ranking system works well for 1v1 board games like chess and checkers. It fairly adjusts player ratings based on match results, making sure stronger players move up while weaker players adjust. It is simple, effective, and a good choice for ranking competitive 1v1 games. However, for Tic-Tac-Toe the ranking adjustments can be ineffective as:
-	Tic-Tac-Toe is a solved game, meaning that at higher ranks, most matches will end in a draw. This makes the ranking system ineffective, as players will rarely gain or lose points. 
-	Since new players may have a higher K-factor, they could gain an unfair advantage when drawing against experienced players, even though both played optimally. 
