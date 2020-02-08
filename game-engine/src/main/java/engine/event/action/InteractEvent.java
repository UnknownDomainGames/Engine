package engine.event.action;

import engine.event.Event;
import org.joml.Vector3fc;

import java.util.Optional;

public interface InteractEvent extends Event {

    Optional<Vector3fc> getInteractionPoint();
}
