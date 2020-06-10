package com.alvarlagerlof.quake2;

import org.bukkit.Material;

import java.util.List;
import java.util.ArrayList;

public class MaterialLists {

    public List<Material> getPressurePlateMaterials() {
        List<Material> materials = new ArrayList<Material>();
    
        materials.add(Material.TRIPWIRE);
        materials.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        materials.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        materials.add(Material.DARK_OAK_PRESSURE_PLATE);
        materials.add(Material.ACACIA_PRESSURE_PLATE);
        materials.add(Material.STONE_PRESSURE_PLATE);
        materials.add(Material.SPRUCE_PRESSURE_PLATE);
        materials.add(Material.BIRCH_PRESSURE_PLATE);
        materials.add(Material.JUNGLE_PRESSURE_PLATE);
        materials.add(Material.OAK_PRESSURE_PLATE);
        materials.add(Material.CAKE);
        materials.add(Material.COBWEB);
        materials.add(Material.SNOW);
    
        return materials;
    }

    public List<Material> getBulletBypassMaterials() {
        List<Material> materials = new ArrayList<Material>();
    
        materials.add(Material.AIR);
        materials.add(Material.WATER);
        materials.add(Material.DARK_OAK_SIGN);
        materials.add(Material.ACACIA_SIGN);
        materials.add(Material.SPRUCE_SIGN);
        materials.add(Material.BIRCH_SIGN);
        materials.add(Material.JUNGLE_SIGN);
        materials.add(Material.OAK_SIGN);
        materials.add(Material.LAVA);

        return materials;
    }
   
}