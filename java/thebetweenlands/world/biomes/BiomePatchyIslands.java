package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorPatchyIslands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.PatchyIslandNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;

public class BiomePatchyIslands
extends BiomeGenBaseBetweenlands
{
	public BiomePatchyIslands(int biomeID) {
		this(biomeID, new BiomeDecoratorPatchyIslands());
	}
	
	public BiomePatchyIslands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT - 2, 0);
		this.setBiomeName("Patchy Islands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new PatchyIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;
	}

	@Override
	public int getRootHeight(int x, int z) {
		return WorldProviderBetweenlands.LAYER_HEIGHT - 2;
	}

	@Override
	public int getHeightVariation(int x, int z) {
		// TODO Auto-generated method stub
		return 0;
	}
}