package moze_intel.projecte.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPedestal extends ModelBase
{
	public ModelRenderer base;
	public ModelRenderer column;
	public ModelRenderer table;

	public ModelPedestal()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.base.addBox(-5.0F, -5.0F, 0.0F, 10, 10, 2, 0.0F);
		this.setRotateAngle(base, 1.5707963267948966F, 0.0F, 0.0F);
		this.column = new ModelRenderer(this, 0, 15);
		this.column.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.column.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 9, 0.0F);
		this.setRotateAngle(column, 1.5707963267948966F, 0.0F, 0.0F);
		this.table = new ModelRenderer(this, 28, 0);
		this.table.setRotationPoint(0.0F, 13.0F, 0.0F);
		this.table.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
		this.setRotateAngle(table, 1.5707963267948966F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.base.render(f5);
		this.column.render(f5);
		this.table.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
