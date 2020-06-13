/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Relax effect implementation.
 * @author JoeAlisson
 */
public final class Relax extends AbstractEffect {
    private final double power;

    private Relax(StatsSet params) {
        power = params.getDouble("power", 0);
        setTicks(params.getInt("ticks"));
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.RELAXING.getMask();
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.RELAXING;
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected)) {
            effected.getActingPlayer().sitDown(false);
        } else {
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
        }
    }

    @Override
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead()) {
            return false;
        }

        if (isPlayer(effected)) {
            if (!effected.getActingPlayer().isSitting()) {
                return false;
            }
        }

        if (effected.getCurrentHp() + 1 > effected.getMaxRecoverableHp()) {
            if (skill.isToggle()) {
                effected.sendPacket(SystemMessageId.THAT_SKILL_HAS_BEEN_DE_ACTIVATED_AS_HP_WAS_FULLY_RECOVERED);
                return false;
            }
        }

        final double manaDam = power * getTicksMultiplier();
        if (manaDam > effected.getCurrentMp()) {
            if (skill.isToggle()) {
                effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
                return false;
            }
        }

        effected.reduceCurrentMp(manaDam);
        return skill.isToggle();
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Relax(data);
        }

        @Override
        public String effectName() {
            return "Relax";
        }
    }
}
