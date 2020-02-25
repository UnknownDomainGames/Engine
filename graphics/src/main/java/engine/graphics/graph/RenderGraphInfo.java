package engine.graphics.graph;

import java.util.List;

public class RenderGraphInfo {
    private String name;
    private String mainTask;
    private List<RenderTaskInfo> tasks;

    public static RenderGraphInfo renderGraph() {
        return new RenderGraphInfo();
    }

    public String getName() {
        return name;
    }

    public RenderGraphInfo setName(String name) {
        this.name = name;
        return this;
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

    public RenderGraphInfo setTasks(RenderTaskInfo task) {
        this.tasks = List.of(task);
        return this;
    }

    public RenderGraphInfo setTasks(RenderTaskInfo... tasks) {
        this.tasks = List.of(tasks);
        return this;
    }
}
