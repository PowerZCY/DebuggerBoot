package com.xyz.caofancpu.util.datastracture.graph;

import com.google.common.collect.Lists;
import com.xyz.caofancpu.util.streamoperateutils.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Robert•Tarjan强连通分量算法
 *
 * @author caofanCPU
 */
public class TarjanSSC<T> {
    /**
     * 有向图
     */
    private DirectedGraph<T> graph;
    /**
     * 有向图强连通分量索引结果
     */
    private List<ArrayList<Integer>> resultIndexList = Lists.newArrayList();
    /**
     * 有向图顶点是否在计算栈中, 打标记方便查找
     */
    private boolean[] inStack;
    /**
     * 栈容器
     */
    private Stack<Integer> stack;
    /**
     * i don't know how to description this amazing array
     */
    private int[] dfn;
    /**
     * i don't know how to description this amazing array either.
     */
    private int[] low;
    /**
     * 时间戳整数
     */
    private int time;

    public TarjanSSC(DirectedGraph<T> graph) {
        this.graph = graph;
        this.inStack = new boolean[graph.getVertexNum()];
        this.stack = new Stack<>();
        dfn = new int[graph.getVertexNum()];
        low = new int[graph.getVertexNum()];
        // 将dfn所有元素都置为-1，其中dfn[i]=-1代表i还有没被访问过。
        Arrays.fill(dfn, -1);
        Arrays.fill(low, -1);
    }

    public List<ArrayList<T>> calculateByIndex() {
        for (int i = 0; i < this.graph.getVertexNum(); i++) {
            if (dfn[i] == -1) {
                tarjan(i);
            }
        }
        List<ArrayList<T>> resultElementList = Lists.newArrayList();
        for (int i = 0; i < resultIndexList.size(); i++) {
            ArrayList<Integer> itemList = resultIndexList.get(i);
            resultElementList.add(new ArrayList<>(itemList.size()));
            resultElementList.get(i).addAll(CollectionUtil.transToList(itemList, index -> graph.getVertexIndexAsKeyMap().get(index)));
        }
        return resultElementList;
    }

    private void tarjan(int current) {
        dfn[current] = low[current] = time++;
        inStack[current] = true;
        stack.push(current);
        ArrayList<Integer> currentList = graph.getVertexIndexList().get(current);
        for (int next : currentList) {
            // -1代表没有被访问
            if (dfn[next] == -1) {
                tarjan(next);
                low[current] = Math.min(low[current], low[next]);
            } else if (inStack[next]) {
                low[current] = Math.min(low[current], dfn[next]);
            }
        }

        if (low[current] == dfn[current]) {
            ArrayList<Integer> temp = new ArrayList<>();
            int j = -1;
            while (current != j) {
                j = stack.pop();
                inStack[j] = false;
                temp.add(j);
            }
            resultIndexList.add(temp);
        }
    }

}
