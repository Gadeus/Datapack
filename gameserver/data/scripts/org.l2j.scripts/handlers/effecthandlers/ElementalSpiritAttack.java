package handlers.effecthandlers;

import org.l2j.gameserver.data.database.elemental.ElementalType;
import org.l2j.gameserver.model.StatsSet;

public class ElementalSpiritAttack extends AbstractStatEffect {

    public ElementalSpiritAttack(StatsSet params) {
        super(params, params.getEnum("type", ElementalType.class).getAttackStat());
    }
}
