package thebetweenlands;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.client.gui.GuiOverlay;
import thebetweenlands.entities.BLEntityRegistry;
import thebetweenlands.event.listener.DebugListener;
import thebetweenlands.event.listener.GenericListener;
import thebetweenlands.event.player.OctineArmorHandler;
import thebetweenlands.event.player.DecayEventHandler;
import thebetweenlands.event.player.TorchPlaceEventHandler;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.MessageAltarCraftingProgress;
import thebetweenlands.network.MessageDruidTeleportParticle;
import thebetweenlands.network.MessageSyncPlayerDecay;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.recipes.RecipeHandler;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import thebetweenlands.world.feature.structure.WorlGenDruidCircle;
import thebetweenlands.world.feature.trees.WorldGenGiantTree;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands
{
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper networkWrapper;

	@Instance(ModInfo.ID)
	public static TheBetweenlands instance;

	/**
	 * True for debug mode
	 * Keys:
	 * - F: Fullbright
	 * - C: Fast flight
	 */
	public static boolean DEBUG = true;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {

		//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);

		//BL Registry
		BLItemRegistry.init();
		BLFluidRegistry.init();
		BLBlockRegistry.init();
		BLEntityRegistry.init();
		BLBiomeRegistry.init();

		GameRegistry.registerWorldGenerator(new WorlGenDruidCircle(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenGiantTree(), 0);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		//TODO: Just temporary to test some stuff
		DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
		DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);

		//Packet Registry
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
		networkWrapper.registerMessage(MessageAltarCraftingProgress.class, MessageAltarCraftingProgress.class, 0, Side.CLIENT);
		networkWrapper.registerMessage(MessageDruidTeleportParticle.class, MessageDruidTeleportParticle.class, 1, Side.CLIENT);
        networkWrapper.registerMessage(MessageSyncPlayerDecay.class, MessageSyncPlayerDecay.class, 2, Side.CLIENT);
        networkWrapper.registerMessage(MessageSyncPlayerDecay.class, MessageSyncPlayerDecay.class, 3, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntities();
		proxy.registerRenderInformation();
		FMLCommonHandler.instance().bus().register(ConfigHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(DecayEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(GenericListener.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLFluidRegistry.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new OctineArmorHandler());
		MinecraftForge.EVENT_BUS.register(new TorchPlaceEventHandler());
        MinecraftForge.EVENT_BUS.register(DecayEventHandler.INSTANCE);

        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new GuiOverlay());
        }

		if(DEBUG) {
			FMLCommonHandler.instance().bus().register(DebugListener.INSTANCE);
			MinecraftForge.EVENT_BUS.register(DebugListener.INSTANCE);
		}

		RecipeHandler.init();
	}
}
