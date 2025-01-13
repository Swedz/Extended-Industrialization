package net.swedz.extended_industrialization;

import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;

public interface EIClientConfig
{
	@ConfigKey("render_tesla_animations")
	@ConfigComment("Whether tesla animations should be rendered or not")
	default boolean renderTeslaAnimations()
	{
		return true;
	}
}
