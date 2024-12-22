package net.swedz.extended_industrialization.client.armor.decorations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;

public abstract class NanoSuitDecorationModel<T extends LivingEntity> extends HumanoidArmorModel<T>
{
	protected final ModelPart          root;
	protected final NanoSuitDecoration decoration;
	
	public NanoSuitDecorationModel(ModelPart root, NanoSuitDecoration decoration)
	{
		super(root);
		this.root = root;
		this.decoration = decoration;
	}
	
	public final boolean test(T entity, EquipmentSlot slot, ItemStack stack)
	{
		return stack.getItem() instanceof NanoSuitArmorItem item &&
			   decoration.equipmentSlot() == slot && decoration.isActiveFor(item, stack);
	}
	
	public abstract void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, EquipmentSlot slot, int packedLight,
								ArmorMaterial.Layer armorLayer, int armorLayerIndex, int armorLayerColor, boolean armorLayerIsColored);
}
