package net.daichang.starlight.server.util;

import java.util.ArrayList;
import java.util.List;

public class GodPlayerList {
    private static final List<String> god = new ArrayList<>();

    public static boolean isGod(Object player){
        return god.contains(player.getClass().getName());
    }

    public static void addGod(Object player){
        god.add(player.getClass().getName());
    }
}
