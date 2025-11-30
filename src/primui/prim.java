import java.util.*;
import java.util.List;

public class prim {
    static class Canh {
        public int u, v, w;
        public Canh(int u, int v, int w) { 
            this.u = u; 
            this.v = v; 
            this.w = w; 
        }
    }

    public static List<Canh> prim(int n, List<Canh> danhSachCanh, int s) {
        List<Canh>[] dsKe = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) dsKe[i] = new ArrayList<>();
        
        for (Canh c : danhSachCanh) {
            if (c.u >= 1 && c.u <= n && c.v >= 1 && c.v <= n) {
                dsKe[c.u].add(new Canh(c.u, c.v, c.w));
                dsKe[c.v].add(new Canh(c.v, c.u, c.w));
            }
        }

        boolean[] used = new boolean[n + 1];
        int[] dis = new int[n + 1];
        int[] parent = new int[n + 1];
        Arrays.fill(dis, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        dis[s] = 0;
        pq.add(new int[]{0, s});
        List<Canh> mst = new ArrayList<>();

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[1];
            if (used[u]) continue;
            used[u] = true;

            if (parent[u] != -1) mst.add(new Canh(parent[u], u, dis[u]));

            for (Canh c : dsKe[u]) {
                int v = c.v, w = c.w;
                if (!used[v] && dis[v] > w) {
                    dis[v] = w;
                    parent[v] = u;
                    pq.add(new int[]{dis[v], v});
                }
            }
        }
        return mst;
    }

    public static int tinhTongChiPhi(List<Canh> cayKhungNhoNhat) {
        int tongChiPhi = 0;
        for (Canh c : cayKhungNhoNhat) tongChiPhi += c.w;
        return tongChiPhi;
    }
}