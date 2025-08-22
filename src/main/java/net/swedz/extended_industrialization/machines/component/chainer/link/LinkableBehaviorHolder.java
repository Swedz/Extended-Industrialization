package net.swedz.extended_industrialization.machines.component.chainer.link;

import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.behavior.BehaviorHolder;

import java.util.List;

public final class LinkableBehaviorHolder extends BehaviorHolder<ChainerLinkable, LinkContext>
{
	public LinkableBehaviorHolder(List<ChainerLinkable> behaviors)
	{
		super(behaviors);
	}
	
	public LinkResult test(LinkContext context)
	{
		if(context.hasBlockState() && context.blockState().is(EITags.Blocks.MACHINE_CHAINER_BLACKLIST))
		{
			return LinkResult.fail(false);
		}
		return this.behavior(context)
				.map((linkable) -> linkable.test(context))
				.orElseGet(() -> LinkResult.fail(false));
	}
}
