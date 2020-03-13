package engine.graphics.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: 一块是渲染缓冲区的结果获取，一块是游戏刻的输入，一块是渲染前的预处理接口，一块是其他任务的调用
public class RenderGraphInfo {
    private String mainTask;
    private final List<RenderTaskInfo> tasks = new ArrayList<>();

    public static RenderGraphInfo renderGraph() {
        return new RenderGraphInfo();
    }

    public String getMainTask() {
        return mainTask;
    }

    public RenderGraphInfo setMainTask(String task) {
        this.mainTask = task;
        return this;
    }

    public List<RenderTaskInfo> getTasks() {
        return tasks;
    }

    public RenderGraphInfo addTasks(RenderTaskInfo task) {
        this.tasks.add(task);
        return this;
    }

    public RenderGraphInfo addTasks(RenderTaskInfo... tasks) {
        Collections.addAll(this.tasks, tasks);
        return this;
    }
}
