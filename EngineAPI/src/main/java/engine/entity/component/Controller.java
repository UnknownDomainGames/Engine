package engine.entity.component;

import engine.component.Component;
import engine.player.Player;

import javax.annotation.Nonnull;

public interface Controller extends Component {

    @Nonnull
    Player getController();
}
