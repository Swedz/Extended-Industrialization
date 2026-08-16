package net.swedz.extended_industrialization;

import net.swedz.tesseract.config.annotation.ConfigComment;
import net.swedz.tesseract.config.annotation.ConfigKey;
import net.swedz.tesseract.config.annotation.Range;

public interface EIClientConfig
{
	@ConfigKey
	@ConfigComment("Whether tesla animations should be rendered or not")
	default boolean renderTeslaAnimations()
	{
		return true;
	}
	
	@ConfigKey
	@ConfigComment({
			"The distance (in blocks) for tesla animations to render within",
			"Set to default (0) to always render"
	})
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int teslaAnimationsRenderDistance()
	{
		return 0;
	}
	
	@ConfigKey
	@ConfigComment("The distance (in blocks) for the tesla linked overlay to render within")
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int teslaLinkedOverlayRenderDistance()
	{
		return 32;
	}
}
