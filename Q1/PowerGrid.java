import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

class PowerLine {
    String cityA;
    String cityB;

    public PowerLine(String cityA, String cityB) {
        this.cityA = cityA;
        this.cityB = cityB;
    }
}


public class PowerGrid {
    int numCities;
    int numLines;
    String[] cityNames;
    PowerLine[] powerLines;
    private HashMap<String, Integer> stringkey;
    private HashMap<String, PowerLine> edges;
    private ArrayList<ArrayList<Integer>> adj;
    private int cnt;
    private int pre[];
    private int low[];
    private ArrayList<PowerLine> criticaledges;
    private boolean mark[];
    private boolean mark2[];
    private int dp[];
    private int h[];
    private int[][] par;
    private int[][] cost;
    public PowerGrid(String filename) throws Exception {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        numCities = Integer.parseInt(br.readLine());
        numLines = Integer.parseInt(br.readLine());
        cityNames = new String[numCities];
        stringkey=new HashMap<String, Integer>();
        edges=new HashMap<String, PowerLine>();
        adj=new ArrayList<ArrayList<Integer>>(numCities);
        for (int i = 0; i < numCities; i++) {
            cityNames[i] = br.readLine();
            stringkey.put(cityNames[i], i);
            adj.add(new ArrayList<Integer>());
        }
        powerLines = new PowerLine[numLines];
        for (int i = 0; i < numLines; i++) {
            String[] line = br.readLine().split(" ");
            powerLines[i] = new PowerLine(line[0], line[1]);
            edges.put(line[0]+line[1], powerLines[i]);
            edges.put(line[1]+line[0], null);
            adj.get(stringkey.get(line[0])).add(stringkey.get(line[1]));
            adj.get(stringkey.get(line[1])).add(stringkey.get(line[0]));
        }
        br.close();
    }

    public ArrayList<PowerLine> criticalLines() {
        criticaledges=new ArrayList<>();
        cnt=0;
        low=new int[numCities];
        pre=new int[numCities];
        for (int v = 0; v < numCities; v++){low[v] = -1;}
        for (int v = 0; v < numCities; v++) {pre[v] = -1;}
        for (int v = 0; v < numCities; v++){
            if (pre[v] == -1){
                dfs(cityNames[v], cityNames[v]);
            }
        }
        return criticaledges;
    }

    public void preprocessImportantLines() {
        mark=new boolean[numCities];
        mark2=new boolean[numCities];
        dp=new int[numCities];
        h=new int[numCities];
        par=new int[18][numCities];
        cost=new int[18][numCities];
        dfs1(cityNames[0]);
        dfs2(cityNames[0]);
        return;
    }

    public int numImportantLines(String cityA, String cityB) {
        return lca(stringkey.get(cityA), stringkey.get(cityB));
    }


    private void dfs(String u, String v){
        pre[stringkey.get(v)]=cnt++;
        low[stringkey.get(v)]=pre[stringkey.get(v)];
        for(int i=0; i<adj.get(stringkey.get(v)).size(); i++){
            if(pre[adj.get(stringkey.get(v)).get(i)]==-1){
                dfs(v, cityNames[adj.get(stringkey.get(v)).get(i)]);
                low[stringkey.get(v)]=Math.min(low[stringkey.get(v)], low[adj.get(stringkey.get(v)).get(i)]);
                if(low[adj.get(stringkey.get(v)).get(i)]==pre[adj.get(stringkey.get(v)).get(i)]){
                    criticaledges.add(edges.get(v+cityNames[adj.get(stringkey.get(v)).get(i)]));
                }
            }
            else if(adj.get(stringkey.get(v)).get(i)!=stringkey.get(u)){
                low[stringkey.get(v)]=Math.min(low[stringkey.get(v)], pre[adj.get(stringkey.get(v)).get(i)]);
            }
        }
    }

    private void dfs1(String v){
        int V=stringkey.get(v);
        mark[V]=true;
        dp[V]=1000000000;
        for(int i=0; i<adj.get(V).size(); i++){
            int u=adj.get(V).get(i);
            if(!mark[u]){
                par[0][u]=V;
                h[u]=h[V]+1;
                dfs1(cityNames[u]);
                if(dp[u]>=h[u]){
                    cost[0][u]=1;
                }
                dp[V]=Math.min(dp[u], dp[V]);
            }
            else if(u!=par[0][V]){
                dp[V]=Math.min(h[u], dp[V]);
            }
        }
    }
    
    private void dfs2(String v){
        int V=stringkey.get(v);
        mark2[V] = true;
        for (int i = 1; i < 18; i++) {
            par[i][V] = par[i-1][par[i-1][V]];
            cost[i][V] = cost[i-1][V] + cost[i-1][par[i-1][V]];
        }
        for (int i=0; i<adj.get(V).size(); i++) {
            if (!mark2[adj.get(V).get(i)]) {
                dfs2(cityNames[adj.get(V).get(i)]);
            }
        }
    }

    private int lca(int u, int v){
        int r = 0;
        if (h[u] > h[v]) {
            int temp = u;
            u = v;
            v = temp;
        }
        for (int d = h[v] - h[u]; d > 0; d -= d & -d) {
            int c = Integer.numberOfTrailingZeros(d);
            r += cost[c][v];
            v = par[c][v];
        }
        if (u == v) {
            return r;
        }
        for (int i = 18 - 1; i >= 0; i--) {
            if (par[i][u] != par[i][v]) {
                r += cost[i][u];
                u = par[i][u];
                r += cost[i][v];
                v = par[i][v];
            }
        }
        r += cost[0][u];
        r += cost[0][v];
        return r;
    }

}