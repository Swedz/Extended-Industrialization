package net.swedz.tesseract.neoforge.compat.guideme.tags.block;

import guideme.compiler.IdUtils;
import guideme.compiler.IndexingContext;
import guideme.compiler.IndexingSink;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.FlowTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytImage;
import guideme.document.flow.InlineBlockAlignment;
import guideme.document.flow.LytFlowInlineBlock;
import guideme.document.flow.LytFlowParent;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.ResourceLocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public final class MarginFloatingImageCompiler extends FlowTagCompiler
{
	private static final Logger LOG = LoggerFactory.getLogger(MarginFloatingImageCompiler.class);
	
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("MarginFloatingImage");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el)
	{
		var src = el.getAttributeString("src", null);
		var align = el.getAttributeString("align", "left");
		var title = el.getAttributeString("title", null);
		int marginLeft = MdxAttrs.getInt(compiler, parent, el, "left", 5);
		int marginTop = MdxAttrs.getInt(compiler, parent, el, "top", 5);
		int marginRight = MdxAttrs.getInt(compiler, parent, el, "right", 5);
		int marginBottom = MdxAttrs.getInt(compiler, parent, el, "bottom", 5);
		
		var image = new LytImage();
		if(title != null)
		{
			image.setTitle(title);
		}
		try
		{
			var imageId = IdUtils.resolveLink(src, compiler.getPageId());
			var imageContent = compiler.loadAsset(imageId);
			if(imageContent == null)
			{
				LOG.error("Couldn't find image {}", src);
				image.setTitle("Missing image: " + src);
			}
			image.setImage(imageId, imageContent);
		}
		catch (ResourceLocationException e)
		{
			LOG.error("Invalid image id: {}", src);
			image.setTitle("Invalid image URL: " + src);
		}
		
		image.setMarginLeft(marginLeft);
		image.setMarginTop(marginTop);
		image.setMarginRight(marginRight);
		image.setMarginBottom(marginBottom);
		
		var inlineBlock = new LytFlowInlineBlock();
		inlineBlock.setBlock(image);
		switch (align)
		{
			case "left" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_LEFT);
			case "right" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_RIGHT);
			default ->
			{
				parent.append(compiler.createErrorFlowContent("Invalid align. Must be left or right.", el));
				return;
			}
		}
		
		parent.append(inlineBlock);
	}
	
	@Override
	public void index(IndexingContext indexer, MdxJsxElementFields el, IndexingSink sink)
	{
		var title = el.getAttributeString("title", null);
		if(title != null)
		{
			sink.appendText(el, title);
		}
	}
}
