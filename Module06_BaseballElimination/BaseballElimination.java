import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {
    private int numberOfTeams;
    private HashMap<String, Integer> teamsIndex = new HashMap<>();
    private Team[] teams;
    private HashMap<String, Queue<String>> eleminatedBy = new HashMap<>();
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        In in = new In(filename);
        numberOfTeams = in.readInt();
        teams = new Team[numberOfTeams];
        for (int i = 0; i < numberOfTeams; i++) {
            String teamName = in.readString();
            int wins = in.readInt();
            int losses = in.readInt();
            int remaining = in.readInt();
            int[] remainingMatches = new int[numberOfTeams];
            for (int j = 0; j < numberOfTeams; j++) {
                remainingMatches[j] = in.readInt();
            }
            Team team = new Team(i, teamName, wins, losses, remaining, remainingMatches);
            teams[i] =  team;
            teamsIndex.put(teamName, i);
            eleminatedBy.put(teamName, null);
        }

        int numberOfVertices = 2 + numberOfVertices(numberOfTeams - 1);
        for (String team : teamsIndex.keySet()) {
            HashMap<Integer, Integer> nodeVertexMapping = new HashMap<>();
            int index = teamsIndex.get(team);
            Queue<String> queue = isTrivialEliminated(index);
            boolean isEliminated = queue != null;
            if (isEliminated) {
                teams[index].setIsEliminated(isEliminated);
            }
            else {
                FlowNetwork flowNetwork = constructFlowNetwork(numberOfVertices, team, nodeVertexMapping);
                FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, numberOfVertices - 1);
                isEliminated = isTeamEliminated(flowNetwork);
                teams[index].setIsEliminated(isEliminated);
                if (isEliminated) {
                    queue = new Queue<>();
                    for (var teamToCheck : nodeVertexMapping.keySet()) {
                        if (fordFulkerson.inCut(teamToCheck)) {
                            var i = nodeVertexMapping.get(teamToCheck);
                            queue.enqueue(teams[i].teamName());
                        }
                    }
                }
            }
            eleminatedBy.put(team, queue);
        }
        
    }

    private int numberOfVertices(int numberOfTeams) {
        int total = 0;
        for (int i = numberOfTeams; i > 0; i--) {
            total += i;
        }

        return total;
    }

    private FlowNetwork constructFlowNetwork(int numberOfVertices, String team, HashMap<Integer, Integer> nodeVertexMap) {
        FlowNetwork flowNetwork = new FlowNetwork(numberOfVertices);
        int teamMaxWins = teams[teamsIndex.get(team)].wins() + teams[teamsIndex.get(team)].remaining();
        int indexToAvoid = teamsIndex.get(team);
        int matchesNodeStart = 1;
        int start = 0;
        int end = numberOfVertices - 1;
        int teamsNodesStart = numberOfVertices - numberOfTeams;
        boolean[] marked = new boolean[numberOfTeams];
        marked[indexToAvoid] = true;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != indexToAvoid){
                nodeVertexMap.put(teamsNodesStart + start, i);
                start++;
            }
        }
        start = 0;
        FlowEdge e;
        for (int v = teamsNodesStart; v < end; v++) {
            Team currentTeam = teams[nodeVertexMap.get(v)];
            int[] teamMatches = currentTeam.remainingGames();
            for (int i = 1; i < numberOfTeams - 1; i++) {
                if (end > v + i) {
                    e = new FlowEdge(matchesNodeStart, v, Integer.MAX_VALUE);
                    flowNetwork.addEdge(e);
                    e = new FlowEdge(matchesNodeStart, v + i, Integer.MAX_VALUE);
                    int currentIndex = nodeVertexMap.get(v + i);
                    flowNetwork.addEdge(e);
                    e = new FlowEdge(start, matchesNodeStart, teamMatches[currentIndex]);
                    flowNetwork.addEdge(e);
                    matchesNodeStart++;
                }
            }
            if (teamMaxWins - currentTeam.wins() < 0) return flowNetwork;
            e = new FlowEdge(v, numberOfVertices - 1, teamMaxWins - currentTeam.wins());
            flowNetwork.addEdge(e);
            marked[nodeVertexMap.get(v)] = true;
        }
        return flowNetwork;
    }
    
    private boolean isTeamEliminated(FlowNetwork flowNetwork) {
        for (var edge : flowNetwork.adj(0)) {
            if (edge.flow() != edge.capacity()) return true;
        }
        return false;
    }
    
    private Queue<String> isTrivialEliminated(int teamIndex) {
        int teamMaxWins = teams[teamIndex].wins() + teams[teamIndex].remaining();
        Queue<String> queue = null;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != teamIndex) {
                if (teams[i].wins() > teamMaxWins) {
                    String teamName = teams[i].teamName();
                    if (queue == null) queue = new Queue<>();
                    queue.enqueue(teamName);
                    return queue;
                }
            }
        }
        return queue;
    }
    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }
    
    // all teams
    public Iterable<String> teams() {
        return teamsIndex.keySet();
    }           
    
    // number of wins for given team
    public int wins(String team) {
        validateArguments(team);
        return teams[teamsIndex.get(team)].wins();
    }
    
    // number of losses for given team
    public int losses(String team) {
        validateArguments(team);
        return teams[teamsIndex.get(team)].losses();
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateArguments(team);
        return teams[teamsIndex.get(team)].remaining();
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateArguments(team1);
        validateArguments(team2);
        int indexTeam1 = teamsIndex.get(team1);
        int[] team2matches = teams[teamsIndex.get(team2)].remainingGames();
        return team2matches[indexTeam1];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateArguments(team);
        var index = teamsIndex.get(team);
        return teams[index].isEliminated();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateArguments(team);
        return eleminatedBy.get(team);
    }

    private void validateArguments(String input) {
        if (input == null) throw new IllegalArgumentException();
        if (!teamsIndex.containsKey(input)) throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        var division = new BaseballElimination("teams4.txt");
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
