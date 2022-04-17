package com.infinityraider.agricraft.plugins.jei;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AgriRecipeCategoryProduce implements IRecipeCategory<IAgriPlant> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/produce");
    public static final RecipeType<IAgriPlant> TYPE = new RecipeType<>(ID, IAgriPlant.class);

    private static final TranslatableComponent TITLE = new TranslatableComponent("agricraft.gui.produce");

    public final IAgriDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TYPE, AgriApi.getPlantRegistry().stream().filter(IAgriPlant::isPlant).collect(Collectors.toList()));
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getWoodCropSticksItem()), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getIronCropSticksItem()), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getObsidianCropSticksItem()), TYPE);
    }

    public AgriRecipeCategoryProduce() {
        this.icon = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/debugger.png"), 0, 0, 16, 16, 16, 16);
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_produce.png"), 0, 0, 128, 128, 128, 128);
    }

    @Override
    public RecipeType<IAgriPlant> getRecipeType() {
        return TYPE;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public Class<IAgriPlant> getRecipeClass() {
        return IAgriPlant.class;
    }

    @Nonnull
    @Override
    public TranslatableComponent getTitle() {
        return TITLE;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    private static final String INPUT_SEED = "input_seed";
    private static final String INPUT_PLANT = "input_plant";
    private static final String OUTPUT = "output";

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IAgriPlant plant, IFocusGroup focuses) {
        // Set shapeless
        builder.setShapeless();

        // inputs
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 49)
                .setSlotName(INPUT_SEED)
                .addIngredient(VanillaTypes.ITEM, plant.toItemStack());
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 8)
                .setSlotName(INPUT_PLANT)
                .setCustomRenderer(AgriIngredientPlant.TYPE, AgriIngredientPlant.RENDERER)
                .addIngredient(AgriIngredientPlant.TYPE, plant);

        // outputs
        List<ItemStack> products = Lists.newArrayList();
        plant.getAllPossibleProducts(products::add);
        int index = 0;
        for (int y = 32; y < 82; y += 18) {
            if(index >= products.size()) {
                break;
            }
            for (int x = 74; x < 128; x += 18) {
                if(index < products.size()) {
                    builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                            .setSlotName(OUTPUT + "_" + index)
                            .addIngredient(VanillaTypes.ITEM, products.get(index));
                    index++;
                } else {
                    break;
                }
            }
        }
    }
}
