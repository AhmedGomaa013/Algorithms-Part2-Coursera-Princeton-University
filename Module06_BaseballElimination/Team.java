import java.util.Arrays;

public class Team {
    private int index;
    private int wins;
    private int losses;
    private int remaining;
    private int[] remainingGames;
    private boolean isEliminated;
    private String teamName;

    public Team(int index, String teamName, int wins, int losses, int remaining, int[] remainingGames) {
        this.index = index;
        this.wins = wins;
        this.losses = losses;
        this.remaining = remaining;
        this.remainingGames = Arrays.copyOf(remainingGames, remainingGames.length);
        this.isEliminated = false;
        this.teamName = teamName;
    }

    public int index() {
        return this.index;
    }

    public int wins() {
        return this.wins;
    }

    public int losses() {
        return this.losses;
    }

    public int remaining() {
        return this.remaining;
    }

    public int[] remainingGames() {
        return this.remainingGames;
    }

    public boolean isEliminated() {
         return this.isEliminated;
    }

    public void setIsEliminated(boolean isEliminated) {
        this.isEliminated = isEliminated;
    }

    public String teamName() {
        return this.teamName;
    }
}

