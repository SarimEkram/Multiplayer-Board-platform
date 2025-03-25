package MatchmakingLeaderboard.TicTacToe.Matchmaking;

import MatchmakingLeaderboard.*;

public class TicTacToeMatchmaking extends AbstractTicTacToeMatchmaking{

    boolean matchmakingUp = false;

    MatchmakingQueue queue;

    /**
     * Class that simulates matchmaking
     */
    public TicTacToeMatchmaking() throws Exception {
        try {
            queue = new MatchmakingQueue();
            this.matchmakingUp = true;
        }catch (Exception e){
            this.matchmakingUp = false;
            throw new NetworkFailureException("Matchmaking is Down");
        }
        try {
            while (true) {
                queue.addPlayer(new Player(0.56, 2, 123456, false, new Rank(12),1));
                this.matchmakingUp = true;
                wait(2500);
            }
        }catch (Exception e){
            this.matchmakingUp = false;
            throw new MatchmakingException("Something went wrong!");
        }
    }

    public void startMatchmaking(){
        if (checkMatchmaking()) {
            Player player = queue.getNextPlayer();
            boolean iscompatible = false;
            while (iscompatible) {
                iscompatible = checkPlayers(player, queue.getNextPlayer());
            }
            if (queue.readyToMatch()) {
                findMatch();
                signalStartGame();
            } else {
                System.out.println("Matchmaking cancelled! Not enough players...please try again!");
            }
        } else {
            System.out.println("Matchmaking is down. Please try again in some time. The issue has been reported");
        }
    }

    @Override
    public void joinQueue(Player player) {
        queue.addPlayer(player);
    }

    @Override
    public void leaveQueue(Player player) {
        queue.removePlayer(player);
    }

    @Override
    public void findMatch() {
        System.out.println("Generate a request to the backend asking for an unpopulated game simulation");
    }

    @Override
    public void signalStartGame() {
        System.out.println("Signal game system to start the game simulation and load matched players");
    }

    @Override
    public boolean checkMatchmaking() {
        return this.matchmakingUp;
    }

    @Override
    public void signalAddPlayer(Player player) {
        System.out.printf("Signal database to add player %d ", player.getUserID());
    }

    @Override
    public boolean checkPlayers(Player player1, Player player2) {
        if ((player1.getRank() == player2.getRank()) && (player1.getGameSignal() == player2.getGameSignal())){
            if (Math.abs((player1.getLevel() - player2.getLevel())) == 10 ){
                return true;
            }
        }
        return false;
    }
}
