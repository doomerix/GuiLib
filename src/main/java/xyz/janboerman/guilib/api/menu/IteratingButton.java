package xyz.janboerman.guilib.api.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Generalization of {@link CycleButton}. Cycles through states generated by the UnaryOperator
 * @param <T> the type of the state
 * @param <MH> the menu type
 */
public class IteratingButton<T, MH extends MenuHolder<?>> extends ItemButton<MH> {

    protected T currentState;
    protected UnaryOperator<T> stateUpdater;

    /**
     * Creates an IteratingButton with just an icon. Subclasses must either set the {@link #currentState} and {@link #stateUpdater} fields,
     * or alternatively override {@link #getCurrentState()} and {@link #updateCurrentState(MenuHolder, InventoryClickEvent)}.
     * @param icon the icon
     */
    protected IteratingButton(ItemStack icon) {
        super(icon);
    }

    /**
     * Creates the button.
     * @param icon the icon
     * @param initialState the initial state
     * @param stateUpdater the state update function
     */
    public IteratingButton(ItemStack icon, T initialState, UnaryOperator<T> stateUpdater) {
        super(icon);
        this.currentState = initialState;
        this.stateUpdater = Objects.requireNonNull(stateUpdater, "StateUpdater cannot be null");
    }

    /**
     * Get the current state.
     * @return the current state
     */
    public T getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state to a new state.
     * @param newState the new current state
     */
    protected void setCurrentState(T newState) {
        this.currentState = newState;
    }

    /**
     * Updates the internal state.
     * @param menuHolder the MenuHolder
     * @param event the event that caused the state update
     */
    protected void updateCurrentState(MH menuHolder, InventoryClickEvent event) {
        setCurrentState(stateUpdater.apply(getCurrentState()));
    }

    /**
     * Toggles this button. Subclasses can add extra side-effects before and after updating the current state by overriding.
     * {@link #beforeToggle(MenuHolder, InventoryClickEvent)} and {@link #afterToggle(MenuHolder, InventoryClickEvent)}.
     * @param holder the MenuHolder
     * @param event the InventoryClickEvent
     */
    @Override
    public final void onClick(MH holder, InventoryClickEvent event) {
        boolean toggleSuccess = tryToggle(holder, event);
        event.setCurrentItem(this.stack = updateIcon(holder, event, toggleSuccess));
    }

    /**
     * Toggles the button if allowed by {@link #beforeToggle(MenuHolder, InventoryClickEvent)}.
     * @see #afterToggle(MenuHolder, InventoryClickEvent)
     * @param menuHolder
     * @param event
     * @return whether the button could be toggled
     */
    protected boolean tryToggle(MH menuHolder, InventoryClickEvent event) {
        if (!beforeToggle(menuHolder, event)) return false;
        this.updateCurrentState(menuHolder, event);
        afterToggle(menuHolder, event);
        return true;
    }

    /**
     * Check whether the button can be toggled.
     * The default implementation always return true.
     * @param menuHolder the inventory holder for the menu
     * @param event the InventoryClickEvent that caused the button to toggle
     * @return true
     */
    public boolean beforeToggle(MH menuHolder, InventoryClickEvent event) {
        return true;
    }

    /**
     * Run a side-effect after the button is toggled.
     * The default implementation does nothing.
     * @param menuHolder the inventory holder for the menu
     * @param event the InventoryClickEvent that caused the button to toggle
     */
    public void afterToggle(MH menuHolder, InventoryClickEvent event) {
    }

    /**
     * Determines what the icon should look like.
     * Implementations can override this method.
     * @return the updated icon
     */
    public ItemStack updateIcon(MH menuHolder, InventoryClickEvent event, boolean toggleSuccess) {
        return getIcon();
    }
}
