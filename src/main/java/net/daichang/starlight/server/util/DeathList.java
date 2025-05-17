package net.daichang.starlight.server.util;

import net.daichang.starlight.StarlightMod;

import java.util.ArrayList;
import java.util.List;

public class DeathList {
    private static final List<String> deathList = new ArrayList<>();

    public static boolean isDead(Object entityClass) {
        return deathList.contains(entityClass.getClass().getName());
    }

    public static void addDead(Object entityClass) {
        if (!entityClass.getClass().getName().startsWith("net.daichang.starlight")) deathList.add(entityClass.getClass().getName());
    }

    public static void clearAll(){
        deathList.clear();
        StarlightMod.INFO("DeathList now is clear");
    }

    public static void removeDead(Object entityClass) {
        deathList.remove(entityClass.getClass().getName());
    }

    public static List<String> getDeathList() {
        return deathList;
    }
}
