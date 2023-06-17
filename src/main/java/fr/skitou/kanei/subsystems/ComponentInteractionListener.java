package fr.skitou.kanei.subsystems;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.subsystems.AbstractSubsystem;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ComponentInteractionListener extends AbstractSubsystem {

    @Getter
    private static final Map<String, Consumer<StringSelectInteractionEvent>> selectMenuInteraction = new HashMap<>();

    @Getter
    private static final Map<String, Runnable> buttonInteraction = new HashMap<>();

    public static StringSelectMenu createStringSelectMenuInteraction(StringSelectMenu selectMenu, Consumer<StringSelectInteractionEvent> function) {
        String id = UUID.randomUUID().toString();
        selectMenuInteraction.put(id, function);
        return selectMenu.createCopy().setId(id).build();
    }

    public static Button createButtonInteraction(Button button, Runnable runnable) {
        String id = UUID.randomUUID().toString();
        buttonInteraction.put(id, runnable);
        return button.withId(id);
    }

    public static EntitySelectMenu createEntitySelectMenuInteraction(EntitySelectMenu selectMenu, Consumer<StringSelectInteractionEvent> function) {
        String id = UUID.randomUUID().toString();
        selectMenuInteraction.put(id, function);
        return selectMenu.createCopy().setId(id).build();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "ButtonInteractionListener";
    }

    @Override
    public @NotNull String getDescription() {
        return "Allow interaction with buttons.";
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (selectMenuInteraction.containsKey(event.getComponentId())) {
            selectMenuInteraction.get(event.getComponentId()).accept(event);
            BotInstance.logger.info("Trigger StringSelectInteractionEvent(" + event.getComponentId() + ").");

            selectMenuInteraction.remove(event.getComponentId());
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (buttonInteraction.containsKey(event.getComponentId())) {
            buttonInteraction.get(event.getComponentId()).run();
            BotInstance.logger.info("Trigger ButtonInteraction(" + event.getComponentId() + ").");
            buttonInteraction.remove(event.getComponentId());
        }
    }
}
