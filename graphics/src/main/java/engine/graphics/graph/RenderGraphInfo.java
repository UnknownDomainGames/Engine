package engine.graphics.graph;

import java.util.List;

public class RenderGraphInfo {
    private String name;
    private String mainTask;
    private List<RenderTaskInfo> tasks;

    public static RenderGraphInfo renderGraph() {
        return new RenderGraphInfo();
    }

    public RenderGraphInfo name(String name) {
        this.name = name;
        return this;
    }

    public RenderGraphInfo mainTask(String task) {
        this.mainTask = task;
        return this;
    }

    public RenderGraphInfo tasks(RenderTaskInfo task) {
        this.tasks = List.of(task);
        return this;
    }

    public RenderGraphInfo tasks(RenderTaskInfo... tasks) {
        this.tasks = List.of(tasks);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getMainTask() {
        return mainTask;
    }

    public List<RenderTaskInfo> getTasks() {
        return tasks;
    }
}
