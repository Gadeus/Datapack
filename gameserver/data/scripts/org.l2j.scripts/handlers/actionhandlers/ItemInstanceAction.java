package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;

public class ItemInstanceAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		final Castle castle = CastleManager.getInstance().getCastle(target);
		if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getResidenceId(), target.getId()) != null))
		{
			if ((player.getClan() == null) || (castle.getOwnerId() != player.getClanId()) || !player.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
				player.setTarget(target);
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				return false;
			}
		}
		
		if (!player.isFlying())
		{
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, target);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ItemInstance;
	}
}