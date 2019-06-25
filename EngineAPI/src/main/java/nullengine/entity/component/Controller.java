package nullengine.entity.component;

import nullengine.component.Component;
import nullengine.player.Player;

import javax.annotation.Nonnull;

public interface Controller extends Component {

    @Nonnull
    Player getController();
}
