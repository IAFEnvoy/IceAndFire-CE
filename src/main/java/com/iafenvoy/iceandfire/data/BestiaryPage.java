package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.item.BestiaryItem;
import com.iafenvoy.iceandfire.item.component.BestiaryPageComponent;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public record BestiaryPage(String name, int pageCount) {
    public static List<BestiaryPage> possiblePages(ItemStack book) {
        if (book.getItem() instanceof BestiaryItem) {
            BestiaryPageComponent component = book.get(IafDataComponents.BESTIARY_PAGES.get());
            if (component == null) return List.of();
            List<BestiaryPage> pages = IafRegistries.BESTIARY_PAGE.stream().collect(Collectors.toList());
            pages.removeAll(component.pages().stream().toList());
            return pages;
        }
        return Collections.emptyList();
    }

    public static void addPage(BestiaryPage page, ItemStack book) {
        if (book.getItem() instanceof BestiaryItem) {
            BestiaryPageComponent component = book.get(IafDataComponents.BESTIARY_PAGES.get());
            if (component == null) return;
            List<BestiaryPage> pages = component.pages();
            if (!pages.contains(page)) {
                pages = new LinkedList<>(pages);// mutable copy
                pages.add(page);
                book.set(IafDataComponents.BESTIARY_PAGES.get(), new BestiaryPageComponent(pages));
            }
        }
    }
}
