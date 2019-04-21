package unknowndomain.engine.entity.component;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.player.Player;

import javax.annotation.Nonnull;

public interface Controller extends Component {

    @Nonnull
    Player getController();
}
