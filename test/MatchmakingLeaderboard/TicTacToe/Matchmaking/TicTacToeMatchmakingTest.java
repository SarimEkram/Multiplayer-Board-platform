package MatchmakingLeaderboard.TicTacToe.Matchmaking;

import MatchmakingLeaderboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains the tests for TicTacToeMatchmaking.java
 * Author: Your Name
 */
public class TicTacToeMatchmakingTest {
    private TicTacToeMatchmaking matchmaking;
    private Player player1, player2;
    private int gameType = 1;

    @BeforeEach
    public void setup() throws IOException {
        matchmaking = new TicTacToeMatchmaking();
        player1 = new Player("Player1",10,123456);
        player2 = new Player("Player2", 15, 987654);
    }

    @Test()
    public void testConstructor_NetworkFailureException() {
        for (int i = 0; i < 100; i++) {
            try {
                new TicTacToeMatchmaking();
            }
            catch (NetworkFailureException e) {
                System.out.println("A Network Failure Exception Detected");
                break;
            }
            catch (IOException ignored) {}
        }
    }

    @Test
    public void testJoinQueue() {
        matchmaking.joinQueue(player1);
    }

    @Test
    public void testLeaveQueue() {
        matchmaking.joinQueue(player1);
        matchmaking.leaveQueue(player1);
    }

    @Test
    public void testCheckMatchmaking_Up() {
        assertTrue(matchmaking.checkMatchmaking());
    }

    @Test
    public void testCheckMatchmaking_Down() throws Exception {
        matchmaking = new TicTacToeMatchmaking();
        matchmaking.matchmakingUp = false;
        assertFalse(matchmaking.checkMatchmaking());
    }

    @Test
    public void testCheckPlayers_Compatible() {
        player1.setRank(new Rank(6), gameType);
        player2.setRank(new Rank(6), gameType);
        player1.setGameSignal(1, gameType);
        player2.setGameSignal(1, gameType);
        player1.setLevel(20);
        player2.setLevel(26);

        System.out.println(player1.getRank(gameType));
        System.out.println(player2.getRank(gameType));
        System.out.println(player1.getGameSignal(gameType));
        System.out.println(player2.getGameSignal(gameType));
        System.out.println(player1.getRank(gameType));
        System.out.println(player2.getRank(gameType));
        assertTrue(matchmaking.checkPlayers(player1, player2));
    }

    @Test
    public void testCheckPlayers_NotCompatibleOne() {
        player1.setRank(new Rank(17), 1);
        player2.setRank(new Rank(6), 1);
        assertFalse(matchmaking.checkPlayers(player1, player2));
    }

    @Test
    public void testFindMatch() {
        matchmaking.findMatch(player1, player2);
    }

    @Test
    public void testSignalStartGame() {
        matchmaking.signalStartGame();
    }

    @Test
    public void testSignalAddPlayer() {
        matchmaking.signalAddPlayer(player1);
    }
}
