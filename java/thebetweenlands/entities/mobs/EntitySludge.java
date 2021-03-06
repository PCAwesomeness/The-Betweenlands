package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;

public class EntitySludge extends EntityMob {
	private int sludgeJumpDelay;
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;

	public EntitySludge(World world) {
		super(world);
		isImmuneToFire = true;
		setSize(1.0F, 1.0F);
		sludgeJumpDelay = rand.nextInt(20) + 10;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	/*
	 * @Override protected String getLivingSound() { return ""; }
	 *
	 * @Override protected String getHurtSound() { return ""; }
	 *
	 * @Override protected String getDeathSound() { return ""; }
	 */

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (rand.nextInt(5) == 0)
			entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SLUDGE_BALL, 1 + looting), 0.0F);
	}

	@Override
	public void onUpdate() {
		squishFactor += (squishAmount - squishFactor) * 0.5F;
		prevSquishFactor = squishFactor;
		boolean flag = onGround;
		super.onUpdate();
		if (onGround && !flag) {
			squishAmount = -0.5F;
			//TODO Make some nice particles I guess
		/*	for (int j = 0; j < 8; ++j) {
				float f = rand.nextFloat() * (float) Math.PI * 2.0F;
				float f1 = rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * 0.5F * f1;
				float f3 = MathHelper.cos(f) * 0.5F * f1;
				worldObj.spawnParticle("cloud", posX + f2, boundingBox.minY, posZ + f3, 0.0D, 0.0D, 0.0D);
			}*/
			playSound("mob.slime.big", 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F); 
		} else if (!onGround && flag)
			squishAmount = 1.0F;

		alterSquishAmount();
	}

	protected void alterSquishAmount() {
		squishAmount *= 0.8F;
	}

	@Override
	protected void updateEntityActionState() {
		despawnEntity();
		EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);

		if (entityplayer != null) {
			faceEntity(entityplayer, 10.0F, 20.0F);
			setAttackTarget(entityplayer);
		}

		if (onGround && sludgeJumpDelay-- <= 0) {
			sludgeJumpDelay = getJumpDelay();
			if (entityplayer != null)
				sludgeJumpDelay /= 3;
			isJumping = true;
			moveStrafing = 1.0F - rand.nextFloat() * 2.0F;
			moveForward = 1;
			playSound("mob.slime.big", 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);  
		} else {
			isJumping = false;
			if (onGround)
				moveStrafing = moveForward = 0.0F;
		}
	}

	protected int getJumpDelay() {
		return rand.nextInt(20) + 10;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!player.capabilities.isCreativeMode && !worldObj.isRemote)
			if (player.boundingBox.maxY >= boundingBox.minY && player.boundingBox.minY <= boundingBox.maxY)
				player.attackEntityFrom(DamageSource.causeMobDamage(this), 1F);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int((float) damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}
}
