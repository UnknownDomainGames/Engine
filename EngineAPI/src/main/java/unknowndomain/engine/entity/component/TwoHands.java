package unknowndomain.engine.entity.component;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public interface TwoHands extends Component {

    @Nonnull
    ItemStack getMainHand();

    void setMainHand(@Nonnull ItemStack mainHand);

    @Nonnull
    ItemStack getOffHand();

    void setOffHand(@Nonnull ItemStack offHand);

    class Impl implements TwoHands {

        private ItemStack mainHand = ItemStack.EMPTY;
        private ItemStack offHand = ItemStack.EMPTY;

        @Nonnull
        @Override
        public ItemStack getMainHand() {
            return mainHand;
        }

        @Override
        public void setMainHand(@Nonnull ItemStack mainHand) {
            this.mainHand = Objects.requireNonNull(mainHand);
        }

        @Nonnull
        @Override
        public ItemStack getOffHand() {
            return offHand;
        }

        @Override
        public void setOffHand(@Nonnull ItemStack offHand) {
            this.offHand = Objects.requireNonNull(offHand);
        }
    }
}
