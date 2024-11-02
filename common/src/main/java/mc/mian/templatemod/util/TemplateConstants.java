package mc.mian.templatemod.util;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateConstants {
    public static final Logger LOGGER = LogManager.getLogger(TemplateConstants.MOD_ID);

    public static final String MOD_ID = "templatemod";
    public static final String MOD_DISPLAY_NAME = "Template Mod";

    public static final ResourceLocation TEMPLATE_SOUND = TemplateUtil.modLoc("scratch");
    public static final ResourceLocation TEMPLATE_SOUND_2 = TemplateUtil.modLoc("meow");
    public static final ResourceLocation TEMPLATE_SOUND_3 = TemplateUtil.modLoc("fail");
}
