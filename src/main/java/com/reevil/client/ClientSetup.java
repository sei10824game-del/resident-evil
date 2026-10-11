package com.reevil.client;

import com.reevil.ReEvilMod;
import com.reevil.client.zombie.ZombieModel;
import com.reevil.client.zombie.ZombieRenderer;
import com.reevil.client.crimsonhead.CrimsonHeadModel;
import com.reevil.client.crimsonhead.CrimsonHeadRenderer;
import com.reevil.client.cerberus.CerberusModel;
import com.reevil.client.cerberus.CerberusRenderer;
import com.reevil.client.crow.CrowModel;
import com.reevil.client.crow.CrowRenderer;
import com.reevil.client.hunter.HunterModel;
import com.reevil.client.hunter.HunterRenderer;
import com.reevil.client.chimera.ChimeraModel;
import com.reevil.client.chimera.ChimeraRenderer;
import com.reevil.client.giantspider.GiantSpiderModel;
import com.reevil.client.giantspider.GiantSpiderRenderer;
import com.reevil.client.webspinner.WebSpinnerModel;
import com.reevil.client.webspinner.WebSpinnerRenderer;
import com.reevil.client.neptune.NeptuneModel;
import com.reevil.client.neptune.NeptuneRenderer;
import com.reevil.client.yawn.YawnModel;
import com.reevil.client.yawn.YawnRenderer;
import com.reevil.client.plant42.Plant42Model;
import com.reevil.client.plant42.Plant42Renderer;
import com.reevil.client.tyrant.TyrantModel;
import com.reevil.client.tyrant.TyrantRenderer;
import com.reevil.client.lisatrevor.LisaTrevorModel;
import com.reevil.client.lisatrevor.LisaTrevorRenderer;
import com.reevil.client.licker.LickerModel;
import com.reevil.client.licker.LickerRenderer;
import com.reevil.client.mrx.MrXModel;
import com.reevil.client.mrx.MrXRenderer;
import com.reevil.client.birkin.BirkinModel;
import com.reevil.client.birkin.BirkinRenderer;
import com.reevil.client.alligator.AlligatorModel;
import com.reevil.client.alligator.AlligatorRenderer;
import com.reevil.client.ivy.IvyModel;
import com.reevil.client.ivy.IvyRenderer;
import com.reevil.client.giantmoth.GiantMothModel;
import com.reevil.client.giantmoth.GiantMothRenderer;
import com.reevil.client.larva.LarvaModel;
import com.reevil.client.larva.LarvaRenderer;
import com.reevil.client.nemesis.NemesisModel;
import com.reevil.client.nemesis.NemesisRenderer;
import com.reevil.client.gravedigger.GraveDiggerModel;
import com.reevil.client.gravedigger.GraveDiggerRenderer;
import com.reevil.client.draindeimos.DrainDeimosModel;
import com.reevil.client.draindeimos.DrainDeimosRenderer;
import com.reevil.client.brainsucker.BrainSuckerModel;
import com.reevil.client.brainsucker.BrainSuckerRenderer;
import com.reevil.client.palehead.PaleHeadModel;
import com.reevil.client.palehead.PaleHeadRenderer;
import com.reevil.client.hunterbeta.HunterBetaModel;
import com.reevil.client.hunterbeta.HunterBetaRenderer;
import com.reevil.client.huntergamma.HunterGammaModel;
import com.reevil.client.huntergamma.HunterGammaRenderer;
import com.reevil.client.ganado.GanadoModel;
import com.reevil.client.ganado.GanadoRenderer;
import com.reevil.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReEvilMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(ZombieModel.LAYER, ZombieModel::createBodyLayer);
        e.registerLayerDefinition(CrimsonHeadModel.LAYER, CrimsonHeadModel::createBodyLayer);
        e.registerLayerDefinition(CerberusModel.LAYER, CerberusModel::createBodyLayer);
        e.registerLayerDefinition(CrowModel.LAYER, CrowModel::createBodyLayer);
        e.registerLayerDefinition(HunterModel.LAYER, HunterModel::createBodyLayer);
        e.registerLayerDefinition(ChimeraModel.LAYER, ChimeraModel::createBodyLayer);
        e.registerLayerDefinition(GiantSpiderModel.LAYER, GiantSpiderModel::createBodyLayer);
        e.registerLayerDefinition(WebSpinnerModel.LAYER, WebSpinnerModel::createBodyLayer);
        e.registerLayerDefinition(NeptuneModel.LAYER, NeptuneModel::createBodyLayer);
        e.registerLayerDefinition(YawnModel.LAYER, YawnModel::createBodyLayer);
        e.registerLayerDefinition(Plant42Model.LAYER, Plant42Model::createBodyLayer);
        e.registerLayerDefinition(TyrantModel.LAYER, TyrantModel::createBodyLayer);
        e.registerLayerDefinition(LisaTrevorModel.LAYER, LisaTrevorModel::createBodyLayer);
        e.registerLayerDefinition(LickerModel.LAYER, LickerModel::createBodyLayer);
        e.registerLayerDefinition(MrXModel.LAYER, MrXModel::createBodyLayer);
        e.registerLayerDefinition(BirkinModel.LAYER, BirkinModel::createBodyLayer);
        e.registerLayerDefinition(AlligatorModel.LAYER, AlligatorModel::createBodyLayer);
        e.registerLayerDefinition(IvyModel.LAYER, IvyModel::createBodyLayer);
        e.registerLayerDefinition(GiantMothModel.LAYER, GiantMothModel::createBodyLayer);
        e.registerLayerDefinition(LarvaModel.LAYER, LarvaModel::createBodyLayer);
        e.registerLayerDefinition(NemesisModel.LAYER, NemesisModel::createBodyLayer);
        e.registerLayerDefinition(GraveDiggerModel.LAYER, GraveDiggerModel::createBodyLayer);
        e.registerLayerDefinition(DrainDeimosModel.LAYER, DrainDeimosModel::createBodyLayer);
        e.registerLayerDefinition(BrainSuckerModel.LAYER, BrainSuckerModel::createBodyLayer);
        e.registerLayerDefinition(PaleHeadModel.LAYER, PaleHeadModel::createBodyLayer);
        e.registerLayerDefinition(HunterBetaModel.LAYER, HunterBetaModel::createBodyLayer);
        e.registerLayerDefinition(HunterGammaModel.LAYER, HunterGammaModel::createBodyLayer);
        e.registerLayerDefinition(GanadoModel.LAYER, GanadoModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.ZOMBIE.get(), ZombieRenderer::new);
        e.registerEntityRenderer(ModEntities.CRIMSON_HEAD.get(), CrimsonHeadRenderer::new);
        e.registerEntityRenderer(ModEntities.CERBERUS.get(), CerberusRenderer::new);
        e.registerEntityRenderer(ModEntities.CROW.get(), CrowRenderer::new);
        e.registerEntityRenderer(ModEntities.HUNTER.get(), HunterRenderer::new);
        e.registerEntityRenderer(ModEntities.CHIMERA.get(), ChimeraRenderer::new);
        e.registerEntityRenderer(ModEntities.GIANT_SPIDER.get(), GiantSpiderRenderer::new);
        e.registerEntityRenderer(ModEntities.WEB_SPINNER.get(), WebSpinnerRenderer::new);
        e.registerEntityRenderer(ModEntities.NEPTUNE.get(), NeptuneRenderer::new);
        e.registerEntityRenderer(ModEntities.YAWN.get(), YawnRenderer::new);
        e.registerEntityRenderer(ModEntities.PLANT42.get(), Plant42Renderer::new);
        e.registerEntityRenderer(ModEntities.TYRANT.get(), TyrantRenderer::new);
        e.registerEntityRenderer(ModEntities.LISA_TREVOR.get(), LisaTrevorRenderer::new);
        e.registerEntityRenderer(ModEntities.LICKER.get(), LickerRenderer::new);
        e.registerEntityRenderer(ModEntities.MR_X.get(), MrXRenderer::new);
        e.registerEntityRenderer(ModEntities.BIRKIN.get(), BirkinRenderer::new);
        e.registerEntityRenderer(ModEntities.ALLIGATOR.get(), AlligatorRenderer::new);
        e.registerEntityRenderer(ModEntities.IVY.get(), IvyRenderer::new);
        e.registerEntityRenderer(ModEntities.GIANT_MOTH.get(), GiantMothRenderer::new);
        e.registerEntityRenderer(ModEntities.LARVA.get(), LarvaRenderer::new);
        e.registerEntityRenderer(ModEntities.NEMESIS.get(), NemesisRenderer::new);
        e.registerEntityRenderer(ModEntities.GRAVE_DIGGER.get(), GraveDiggerRenderer::new);
        e.registerEntityRenderer(ModEntities.DRAIN_DEIMOS.get(), DrainDeimosRenderer::new);
        e.registerEntityRenderer(ModEntities.BRAIN_SUCKER.get(), BrainSuckerRenderer::new);
        e.registerEntityRenderer(ModEntities.PALE_HEAD.get(), PaleHeadRenderer::new);
        e.registerEntityRenderer(ModEntities.HUNTER_BETA.get(), HunterBetaRenderer::new);
        e.registerEntityRenderer(ModEntities.HUNTER_GAMMA.get(), HunterGammaRenderer::new);
        e.registerEntityRenderer(ModEntities.GANADO.get(), GanadoRenderer::new);
    }

    private ClientSetup() {}
}
