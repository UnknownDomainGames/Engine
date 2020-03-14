package engine.graphics.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
