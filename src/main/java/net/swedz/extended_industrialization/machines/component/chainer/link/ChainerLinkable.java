package net.swedz.extended_industrialization.machines.component.chainer.link;

import net.swedz.tesseract.neoforge.behavior.Behavior;

public interface ChainerLinkable extends Behavior<LinkContext>
{
	LinkResult test(LinkContext context);
}
