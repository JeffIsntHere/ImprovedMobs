package io.github.flemmli97.improvedmobs.mixin;

import io.github.flemmli97.improvedmobs.mixinhelper.IGoalModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin implements IGoalModifier {

    @Shadow
    Set<WrappedGoal> availableGoals;

    @Override
    public void goalRemovePredicate(Predicate<Goal> goal) {
        this.availableGoals.stream().filter(wrappedGoal -> goal.test(wrappedGoal.getGoal())).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
        this.availableGoals.removeIf(wrappedGoal -> goal.test(wrappedGoal.getGoal()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Goal> void modifyGoal(Class<T> clss, Consumer<T> cons) {
        this.availableGoals.stream().filter(prio -> clss.isInstance(prio.getGoal())).forEach(prio -> cons.accept((T) prio.getGoal()));
    }
}