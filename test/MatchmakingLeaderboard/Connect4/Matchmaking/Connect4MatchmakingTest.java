package MatchmakingLeaderboard.Connect4.Matchmaking;

import MatchmakingLeaderboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains the tests for Connect4Matchmaking.java
 */
public class Connect4MatchmakingTest {
    private Connect4Matchmaking matchmaking;
    private Player player1, player2;
    private int gameType = 1;

    @BeforeEach
    public void setup(){
        matchmaking = new Connect4Matchmaking();
        player1 = new Player("Player1",10,123456);
        player2 = new Player("Player2", 15, 987654);
    }

    @Test()
    public void testConstructor_NetworkFailureException() {
        for (int i = 0; i < 100; i++) {
            try {
                matchmaking.matchmakingConnect();
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
    public void testLeaveQueue_EmptyQueue() {
        matchmaking.leaveQueue(player1);
    }


    @Test
    public void testCheckMatchmaking_Up() {
        matchmaking = new Connect4Matchmaking();
        matchmaking.matchmakingUp = true;
        assertTrue(matchmaking.checkMatchmaking());
    }

    @Test
    public void testCheckMatchmaking_Down(){
        matchmaking = new Connect4Matchmaking();
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

        assertTrue(matchmaking.checkPlayers(player1, player2));
    }

    @Test
    public void testCheckPlayers_NotCompatibleOne() {
        player1.setRank(new Rank(17), 2);
        player2.setRank(new Rank(6), 2);
        player1.setGameSignal(2, gameType);
        player2.setGameSignal(2, gameType);
        player1.setLevel(38);
        player2.setLevel(26);
        assertFalse(matchmaking.checkPlayers(player1, player2));
    }

    @Test
    public void testCheckPlayers_NotCompatibleTwo() {
        player1.setRank(new Rank(2500), 2);
        player2.setRank(new Rank(500), 2);
        player1.setGameSignal(2, gameType);
        player2.setGameSignal(2, gameType);
        player1.setLevel(27);
        player2.setLevel(26);
        assertFalse(matchmaking.checkPlayers(player1, player2));
    }

    @Test
    public void testCheckPlayers_NotCompatibleThree() {
        player1.setRank(new Rank(2500), 2);
        player2.setRank(new Rank(500), 2);
        player1.setGameSignal(2, gameType+1);
        player2.setGameSignal(3, gameType+2);
        player1.setLevel(27);
        player2.setLevel(26);
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

    @Test
    public void MatchmakingWithoutPlayers() {
        matchmaking.startMatchmaking();
    }

    @Test
    public void MatchmakingWithOnePlayer() {
        matchmaking.joinQueue(player1);
        matchmaking.startMatchmaking();
    }


}
