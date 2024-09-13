package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.chainer.link.ChainerLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkContext;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkResult;

public final class ChainerRelayLinkable implements ChainerLinkable
{
	@Override
	public boolean matches(LinkContext context)
	{
		return context.hasBlockState() &&
			   context.blockState().is(EITags.Blocks.MACHINE_CHAINER_RELAY);
	}
	
	@Override
	public LinkResult test(LinkContext context)
	{
		return LinkResult.success();
	}
}
