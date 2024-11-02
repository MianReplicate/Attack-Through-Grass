package mc.mian.templatemod.datagen;

import mc.mian.templatemod.common.item.TemplateItems;
import mc.mian.templatemod.util.TemplateConstants;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TemplateItemModelProvider extends ItemModelProvider {
    public TemplateItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, TemplateConstants.MOD_ID, efh);
    }

    @Override
    protected void registerModels() {
        basicItem(TemplateItems.TEMPLATE_ITEM.get());
    }
}
