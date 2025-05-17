package net.daichang.starlight.server.mc;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class StarCommands {
    private static final SimpleCommandExceptionType INVALID_FILTER = new SimpleCommandExceptionType(Component.translatable("commands.forge.entity.list.invalid"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType((dim) -> Component.translatable("commands.forge.entity.list.invalidworld", new Object[]{dim}));
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(Component.translatable("commands.forge.entity.list.none"));

    private StarCommands() {

    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder) Commands.literal("list").requires((cs) -> {
            return cs.hasPermission(2);
        })).then(((RequiredArgumentBuilder)Commands.argument("filter", StringArgumentType.string()).suggests((ctx, builder) -> {
            return SharedSuggestionProvider.suggest(ForgeRegistries.ENTITY_TYPES.getKeys().stream().map(ResourceLocation::toString).map(StringArgumentType::escapeIfRequired), builder);
        }).then(Commands.argument("dim", DimensionArgument.dimension()).executes((ctx) -> {
            return execute((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "filter"), DimensionArgument.getDimension(ctx, "dim").dimension());
        }))).executes((ctx) -> {
            return execute((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "filter"), ((CommandSourceStack)ctx.getSource()).getLevel().dimension());
        }))).executes((ctx) -> {
            return execute((CommandSourceStack)ctx.getSource(), "*", ((CommandSourceStack)ctx.getSource()).getLevel().dimension());
        });
    }

    private static int execute(CommandSourceStack sender, String filter, ResourceKey<Level> dim) throws CommandSyntaxException {
        String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");
        Set<ResourceLocation> names = (Set)ForgeRegistries.ENTITY_TYPES.getKeys().stream().filter((n) -> {
            return n.toString().matches(cleanFilter);
        }).collect(Collectors.toSet());
        if (names.isEmpty()) {
            throw INVALID_FILTER.create();
        } else {
            ServerLevel level = sender.getServer().getLevel(dim);
            if (level == null) {
                throw INVALID_DIMENSION.create(dim);
            } else {
                Map<ResourceLocation, MutablePair<Integer, Map<ChunkPos, Integer>>> list = Maps.newHashMap();
                level.getEntities().getAll().forEach((ex) -> {
                    MutablePair<Integer, Map<ChunkPos, Integer>> info = (MutablePair)list.computeIfAbsent(ForgeRegistries.ENTITY_TYPES.getKey(ex.getType()), (k) -> {
                        return MutablePair.of(0, Maps.newHashMap());
                    });
                    ChunkPos chunk = new ChunkPos(ex.blockPosition());
                    Integer var5 = (Integer)info.left;
                    info.left = (Integer)info.left + 1;
                    ((Map)info.right).put(chunk, (Integer)((Map)info.right).getOrDefault(chunk, 0) + 1);
                });
                if (names.size() != 1) {
                    List<Pair<ResourceLocation, Integer>> info = new ArrayList();
                    list.forEach((key, value) -> {
                        if (names.contains(key)) {
                            Pair<ResourceLocation, Integer> of = Pair.of(key, (Integer)value.left);
                            info.add(of);
                        }

                    });
                    info.sort((a, b) -> {
                        return Objects.equals(a.getRight(), b.getRight()) ? ((ResourceLocation)a.getKey()).toString().compareTo(((ResourceLocation)b.getKey()).toString()) : (Integer)b.getRight() - (Integer)a.getRight();
                    });
                    if (info.size() == 0) {
                        throw NO_ENTITIES.create();
                    } else {
                        int count = info.stream().mapToInt(Pair::getRight).sum();
                        sender.sendSuccess(() -> {
                            return Component.translatable("commands.forge.entity.list.multiple.header", new Object[]{count});
                        }, false);
                        info.forEach((ex) -> {
                            sender.sendSuccess(() -> {
                                Object var10000 = ex.getValue();
                                return Component.literal("  " + var10000 + ": " + ex.getKey());
                            }, false);
                        });
                        return info.size();
                    }
                } else {
                    ResourceLocation name = (ResourceLocation)names.iterator().next();
                    Pair<Integer, Map<ChunkPos, Integer>> info = (Pair)list.get(name);
                    if (info == null) {
                        throw NO_ENTITIES.create();
                    } else {
                        sender.sendSuccess(() -> {
                            return Component.translatable("commands.forge.entity.list.single.header", new Object[]{name, info.getLeft()});
                        }, false);
                        List<Map.Entry<ChunkPos, Integer>> toSort = new ArrayList();
                        toSort.addAll(((Map)info.getRight()).entrySet());
                        toSort.sort((a, b) -> {
                            return Objects.equals(a.getValue(), b.getValue()) ? ((ChunkPos)a.getKey()).toString().compareTo(((ChunkPos)b.getKey()).toString()) : (Integer)b.getValue() - (Integer)a.getValue();
                        });
                        long limit = 10L;
                        Iterator var12 = toSort.iterator();

                        while(var12.hasNext()) {
                            Map.Entry<ChunkPos, Integer> e = (Map.Entry)var12.next();
                            if (limit-- == 0L) {
                                break;
                            }

                            sender.sendSuccess(() -> {
                                Object var10000 = e.getValue();
                                return Component.literal("  " + var10000 + ": " + ((ChunkPos)e.getKey()).x + ", " + ((ChunkPos)e.getKey()).z);
                            }, false);
                        }

                        return toSort.size();
                    }
                }
            }
        }
    }
}
