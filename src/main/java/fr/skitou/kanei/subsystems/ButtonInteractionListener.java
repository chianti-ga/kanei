package fr.skitou.kanei.subsystems;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.subsystems.AbstractSubsystem;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ButtonInteractionListener extends AbstractSubsystem {

    @Getter
    private static final Map<String, Runnable> buttonInteraction = new HashMap<>();

    public static Button createButtonInteraction(Button button, Runnable runnable) {
        String id = UUID.randomUUID().toString();
        buttonInteraction.put(id, runnable);
        return button.withId(id);
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
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (buttonInteraction.containsKey(event.getComponentId())) {
            buttonInteraction.get(event.getComponentId()).run();
            BotInstance.logger.info("Trigger ButtonInteraction(" + event.getComponentId() + ").");
        }
    }
}
