package net.swedz.extended_industrialization.client.armor.decorations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public abstract class NanoArmorDecoration<T extends LivingEntity> extends HumanoidArmorModel<T>
{
	protected final ModelPart     root;
	protected final EquipmentSlot slot;
	
	public NanoArmorDecoration(ModelPart root, EquipmentSlot slot)
	{
		super(root);
		this.root = root;
		this.slot = slot;
	}
	
	public EquipmentSlot slot()
	{
		return slot;
	}
	
	public final boolean test(T entity, EquipmentSlot slot, ItemStack stack)
	{
		return this.slot == slot && this.shouldRender(entity, slot, stack);
	}
	
	protected abstract boolean shouldRender(T entity, EquipmentSlot slot, ItemStack stack);
	
	public abstract void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, EquipmentSlot slot, int packedLight,
								ArmorMaterial.Layer armorLayer, int armorLayerIndex, int armorLayerColor, boolean armorLayerIsColored);
	
	public abstract void copyFrom(HumanoidModel model);
}
