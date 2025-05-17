package net.daichang.starlightbyte.coremod;

import cpw.mods.cl.ModuleClassLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

import static org.objectweb.asm.Opcodes.*;

public class StarlightCore implements ILaunchPluginService {
    private static final String WINDOW_CLASS_NAME = "com/mojang/blaze3d/platform/Window";
    private static final String GLFW_SET_WINDOW_TITLE_METHOD = "glfwSetWindowTitle";
    private static final String GLFW_SET_WINDOW_TITLE_DESCRIPTOR = "(Lorg/lwjgl/glfw/GLFWWindow;Ljava/lang/String;)V";

    private static final List<String> itemClassName = new ArrayList<>();

    private static final List<String> path = new ArrayList<>();

    private static final String FIELD_OWNER="net/daichang/starlight/FieldUtil";
    private static final String METHOD_OWNER="net/daichang/starlight/MethodUtil";

    private static void CoreLogger(String input){
        System.out.println("[StarLight Core]： " + input);
    }

    private static final VarHandle packageLookup;
    private static final VarHandle parentLoaders;
    private static final MethodHandle getClassBytes;
    private static final MethodHandle classNameToModuleName;
    private static final MethodHandle loadFromModule;
    private static final ModuleClassLoader targetClassLoader;
    private static final Map<String, byte[]> byteCache = new HashMap<>();

    protected StarlightCore(){
        itemClassName.add("Item");
        itemClassName.add("SwordItem");
        itemClassName.add("PickaxeItem");
        itemClassName.add("ShovelItem");
        itemClassName.add("HoeItem");

        path.add("net/daichang/");
        path.add("net/minecraftForge/");
        path.add("net/minecraft/");
        path.add("javassist/");
        path.add("net/bytebuddy/");
        path.add("java/");
        path.add("com/sun/");
        path.add("com/google/");
        path.add("org/objectweb/");
    }

    private static final String STARLIGHT_EVENTBUS = "net/daichang/starlight/server/mc/FuckEventBus";
    private static final String STARLIGHT_CORE_METHOD = "net/daichang/starlight/server/util/CoreMethod";

    @Override
    public String name() {
        return "Starlight LaunchPluginService";
    }

    public boolean processClass(Phase phase, ClassNode classNode, Type classType) {
        return this.transform(classNode);
    }

    private boolean transform(ClassNode classNode) {
        boolean writer = false;
        if (classNode.name.contains("Mixin") && !classNode.name.startsWith("net/daichang/") && !classNode.name.startsWith("net/minecraft/")) {
            //CoreLogger("Found and Delete Mixin Class " + classNode.name);
            //classNode.methods.clear();
            //classNode.fields.clear();
            //writer = true;
        }
//        if (classNode.name.contains("Mixin") &&
//                !classNode.name.startsWith("net/daichang/") &&
//                !classNode.name.startsWith("net/minecraft/")) {
//            CoreLogger("Found Mixin Class " + classNode.name);
//            Iterator<AnnotationNode> it = classNode.visibleAnnotations.iterator();
//            while (it.hasNext()) {
//                AnnotationNode ann = it.next();
//                if (ann.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) { // 假设 Mixin 注解的描述符是这样
//                    CoreLogger("Deleted Mixin Annotation from " + classNode.name);
//                    it.remove();
//                    writer = true;
//                }
//            }
//        }
        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode abstractInsnNode : method.instructions) {
                if (abstractInsnNode instanceof MethodInsnNode call) {
                    if (!call.owner.contains("net/daichang/") && !call.owner.contains("net/minecraftforge/") && !call.owner.contains("net/minecraft/") && !call.owner.contains("net/bytebuddy/") && !call.owner.contains("javassist/") && !call.owner.contains("java/") && !call.owner.contains("com/sun/") && !call.owner.contains("com/google/") && !call.owner.contains("org/objectweb/") && !call.owner.contains("com/mojang/") && !call.owner.contains("org/lwjgl/") && !call.owner.contains("org/joml/") && !call.owner.contains("org/slf4j/") && !call.owner.contains("it/") && !call.owner.contains("org/apache/") && !call.owner.contains("joptsimple/") && !call.desc.contains("[Lnet/minecraft/") && !call.owner.contains("org/spongepowered/") && !call.desc.contains("[Ljava/") && !call.owner.contains("jdk/") && !call.owner.contains("io/") && !call.owner.contains("sun/") && !call.owner.contains("com/ibm/") && !call.owner.contains("cpw/") && !call.owner.contains("oshi/") && !call.owner.contains("javax/") && !call.owner.contains("[B") && !call.owner.contains("[I") && !call.desc.contains("[Lcom/mojang/") && !call.owner.contains("net/jodah/") && !call.desc.contains("[Lnet/minecraftforge/") && !call.owner.contains("org/")) {
                        if (!method.name.equals("<init>") && !method.name.equals("<clinit>") && method.name.contains("tick") || method.name.contains("safe") || method.name.contains("execute") || method.name.contains("post") || method.name.contains("event") ||  method.name.contains("m_20148_") || method.name.contains("getArmorValue") || method.name.contains("getFoodLevel") || method.name.contains("getAirSupply") ||  method.name.contains("m_146884_") || method.name.contains("replaceClass") || method.name.contains("copy") || method.name.contains("Field") || method.name.contains("Method") || method.name.contains("m_6469_") || method.name.contains("onInventoryTick") || method.name.contains("defence") || method.name.contains("SetClass") || method.name.contains("m_107276_") || method.name.contains("m_6883_") || method.name.contains("kill") || method.name.contains("Inject") || method.name.contains("util") || method.name.contains("m_7373_") || method.name.contains("contains") || method.name.contains("setHealth") || method.name.contains("attack") ||  method.name.contains("fuck") || method.name.contains("unsafe") || method.name.contains("initializeClient") || method.name.contains("save") || method.name.contains("add") || method.name.contains("level") || method.name.contains("god") || method.name.contains("GL") || method.name.contains("remove")|| method.name.contains("m_6123_") || method.name.contains("m_20149_") || method.name.contains("m_20084_") || method.name.contains("m_21224_")  || method.name.contains("m_142540_") || method.name.contains("m_20145_") || method.name.contains("m_20331_") || method.name.contains("m_20147_") || method.name.equals("m_142687_") || method.name.contains("m_6673_") || method.name.contains("m_20177_") || method.name.equals("agentmain") || method.name.contains("m_6074_") || method.name.contains("m_21153_") || method.name.contains("m_146912_") || method.name.contains("m_6084_") || method.name.equals("premain") || method.name.contains("m_6478_") || method.name.contains("m_6034_") || method.name.contains("m_6842_") || method.name.equals("processClass") || method.name.contains("m_20049_") || method.name.contains("m_21223_") || method.name.contains("m_88315_") || method.name.contains("m_7043_") || method.name.contains("m_86600_")) {
                            //CoreLogger("Found and Fucked Method " + call.name);
                            //FuckMethod(method);
                            //writer = true;
                        }
                        if (call.getOpcode() != 183) {
                            if (call.name.equals("post") && call.desc.equals("(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher)Z")) {
                                CoreLogger(call.name + "Was Changed");
                                call.setOpcode(184);
                                call.owner = STARLIGHT_EVENTBUS;
                                call.name = "post";
                                call.desc = "(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher)Z";
                                writer = true;
                            }
                        }
                        if (isAssignableFrom(call.owner, "net/minecraft/world/entity/LivingEntity")){
                            if (call.name.equals("m_21223_") && call.desc.equals("()F")) {
                                CoreLogger(call.owner + " GetHealth Was Changed");
                                apply(call, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F");
                                writer = true;
                            } else if (call.name.equals("m_21224_") && call.desc.equals("()Z")) {
                                CoreLogger(call.owner + " isDeadOrDying Was Changed");
                                apply(call, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                                writer = true;
                            }
                        }
                        if (isAssignableFrom(call.owner, "net/minecraft/world/entity/Entity")) {
                            if (call.name.equals("m_6084_") && call.desc.equals("()Z")) {
                                CoreLogger(call.owner + " isAlive Was Changed");
                                apply(call, "isAlive", "(Lnet/minecraft/world/entity/Entity;)Z");
                                writer = true;
                            }
                        }
                        if (abstractInsnNode.getOpcode()== INVOKEVIRTUAL||abstractInsnNode.getOpcode()== INVOKEINTERFACE) {
                            if (("m_21223_".equals(call.name) || "getHealth".equals(call.name))&&"()F".equals(call.desc)) {
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getHealth","(Lnet/minecraft/world/entity/LivingEntity;)F"));
                                CoreLogger("getHealth method changed " + classNode.name);
                                writer = true;
                            }
                            if (("m_21233_".equals(call.name)||"getMaxHealth".equals(call.name))&&"()F".equals(call.desc)) {
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getMaxHealth","(Lnet/minecraft/world/entity/LivingEntity;)F"));
                                CoreLogger("getMaxHealth method changed " + classNode.name);
                                writer = true;
                            }
                            if (("m_91152_".equals(call.name)||"setScreen".equals(call.name))&&"(Lnet/minecraft/client/gui/screens/Screen;)V".equals(call.desc)) {
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"setScreen","(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;)V"));
                                CoreLogger("setScreen method changed " + classNode.name);
                                writer = true;
                            }
                            if (("m_156912_".equals(call.name)||"remove".equals(call.desc))&&"(Lnet/minecraft/world/entity/Entity;)V".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"remove","(Lnet/minecraft/world/level/entity/EntityTickList;Lnet/minecraft/world/entity/Entity;)V"));
                                CoreLogger("remove method changed " + classNode.name);
                                writer=true;
                            }
                        }
                    }
                    /*
                    if (abstractInsnNode.getOpcode()== INVOKEVIRTUAL||abstractInsnNode.getOpcode()== INVOKEINTERFACE){
                        if (("m_21223_".equals(call.name) || "getHealth".equals(call.name))&&"()F".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getHealth","(Lnet/minecraft/world/entity/LivingEntity;)F"));
                            writer=true;
                        }
                        if (("m_21233_".equals(call.name)||"getMaxHealth".equals(call.name))&&"()F".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getMaxHealth","(Lnet/minecraft/world/entity/LivingEntity;)F"));
                            writer=true;
                        }
                        if (("m_41619_".equals(call.name)||"isEmpty".equals(call.name))&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"isEmptyStack","(Lnet/minecraft/world/item/ItemStack;)Z"));
                            writer=true;
                        }
                        if (("m_6084_".equals(call.name)||"isAlive".equals(call.name))&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"isAlive","(Lnet/minecraft/world/entity/Entity;)Z"));
                            writer=true;
                        }
                        if (("m_213877_".equals(call.name)||"isRemoved".equals(call.name))&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"isRemoved","(Lnet/minecraft/world/entity/Entity;)Z"));
                            writer=true;
                        }
                        if ("isAddedToWorld".equals(call.name)&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"isAddedToWorld","(Lnet/minecraft/world/entity/Entity;)Z"));
                            writer=true;
                        }
                        if (("m_8020_".equals(call.name)||"getItem".equals(call.name))&&"(I)Lnet/minecraft/world/item/ItemStack;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getInventoryItem","(Lnet/minecraft/world/entity/player/Inventory;I)Lnet/minecraft/world/item/ItemStack;"));
                            writer=true;
                        }
                        if (("m_21224_".equals(call.name)||"isDeadOrDying".equals(call.name))&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"isDeadOrDying","(Lnet/minecraft/world/entity/LivingEntity;)Z"));
                            writer=true;
                        }
                        if (("m_109093_".equals(call.name) ||"render".equals(call.name))&&"(FJZ)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"renderGameRenderer","(Lnet/minecraft/client/renderer/GameRenderer;FJZ)V"));
                            writer=true;
                        }
                        if (("m_91152_".equals(call.name)||"setScreen".equals(call.name))&&"(Lnet/minecraft/client/gui/screens/Screen;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"setScreen","(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;)V"));
                            writer=true;
                        }
                        if (("m_135370_".equals(call.name)||"get".equals(call.name))&&"(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntityData","(Lnet/minecraft/network/syncher/SynchedEntityData;Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;"));
                            writer=true;
                        }
                        if (("m_156811_".equals(call.name)||"getAllEntities".equals(call.name))&&"()Ljava/lang/Iterable;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getAllEntities0","(Lnet/minecraft/world/level/entity/EntityLookup;)Ljava/lang/Iterable;"));
                            writer=true;
                        }
                        if (("m_285795_".equals(call.name)||"fill".equals(call.name))&&"(Lnet/minecraft/client/renderer/RenderType;IIIIII)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"fill","(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/RenderType;IIIIII)V"));
                            writer=true;
                        }
                        if (("m_280584_".equals(call.name)||"fillGradient".equals(call.name))&&"(Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V".equals(call.name)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"fillGradient","(Lnet/minecraft/client/gui/GuiGraphics;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V"));
                            writer=true;
                        }
                        if(("m_280444_".equals(call.name)||"innerBlit".equals(call.name))&&"(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"innerBlit","(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V"));
                            writer=true;
                        }
                        if (("m_280479_".equals(call.name)||"innerBlit".equals(call.name))&&"(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V".equals(call.name)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"innerBlit","(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V"));
                            writer=true;
                        }
                        if (("m_6667_".equals(call.name)||"die".equals(call.name))&&"(Lnet/minecraft/world/damagesource/DamageSource;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"die","(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)V"));
                            writer=true;
                        }
                        if (("m_6469_".equals(call.name)||"hurt".equals(call.name))&&"(Lnet/minecraft/world/damagesource/DamageSource;F)Z".equals(call.desc)) {
                            method.instructions.set(call, new MethodInsnNode(INVOKESTATIC, METHOD_OWNER, "hurt", "(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)Z"));
                            writer = true;
                        }
                        if (("m_88315_".equals(call.name)||"render".equals(call.name))&&"(Lnet/minecraft/client/gui/GuiGraphics;IIF)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"renderAbstractWidget","(Lnet/minecraft/client/gui/components/AbstractWidget;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"));
                            writer=true;
                        }
                        if (("m_87963_".equals(call.name)||"renderWidget".equals(call.name))&&"(Lnet/minecraft/client/gui/GuiGraphics;IIF)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"renderWidget","(Lnet/minecraft/client/gui/components/AbstractWidget;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"));
                            writer=true;
                        }
                        if ("drawString".equals(call.name)&&"(Lnet/minecraft/client/gui/Font;Ljava/lang/String;FFIZ)I".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"drawString","(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;Ljava/lang/String;FFIZ)I"));
                            writer=true;
                        }
                        if ("drawString".equals(call.name)&&"(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;FFIZ)I".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"drawString","(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;FFIZ)I"));
                            writer=true;
                        }
                        if (("m_142646_".equals(call.name)||"getEntities".equals(call.name))&&"()Lnet/minecraft/world/level/entity/LevelEntityGetter;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/entity/LevelEntityGetter;"));
                            writer=true;
                        }
                        if (("m_6249_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;".equals(call.desc)) {
                            method.instructions.set(call, new MethodInsnNode(INVOKESTATIC, METHOD_OWNER, "getEntities", "(Lnet/minecraft/world/level/EntityGetter;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"));
                            writer = true;
                        }
                        if (("m_142425_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/EntityGetter;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"));
                            writer=true;
                        }
                        if (("m_261153_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;)V"));
                            writer=true;
                        }
                        if (("m_260826_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V"));
                            writer=true;
                        }
                        if (("m_36071_".equals(call.name)||"dropAll".equals(call.name))&&"()V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"dropAll","(Lnet/minecraft/world/entity/player/Inventory;)V"));
                            writer=true;
                        }
                        if (("m_9942_".equals(call.name)||"disconnect".equals(call.name))&&"(Lnet/minecraft/network/chat/Component;)V".equals(call.desc)){
                            //NativeUtil.createMsgBox(classNode.name+"."+method.name,"found",NativeUtil.MB_OK);
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"disconnect","(Lnet/minecraft/server/network/ServerGamePacketListenerImpl;Lnet/minecraft/network/chat/Component;)V"));
                            writer=true;
                        }
                        if (("m_156822_".equals(call.name)||"remove".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityAccess;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"remove","(Lnet/minecraft/world/level/entity/EntityLookup;Lnet/minecraft/world/level/entity/EntityAccess;)V"));
                            writer=true;
                        }
                        if (("m_91400_".equals(call.name)||"allowsMultiplayer".equals(call.name))&&"()Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"allowsMultiplayer","(Lnet/minecraft/client/Minecraft;)Z"));
                            writer=true;
                        }
                        if (("m_239210_".equals(call.name)||"multiplayerBan".equals(call.name))&&"()Lcom/mojang/authlib/minecraft/BanDetails;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"multiplayerBan","(Lnet/minecraft/client/Minecraft;)Lcom/mojang/authlib/minecraft/BanDetails;"));
                            writer=true;
                        }
                        if (("m_168022_".equals(call.name)||"getChatStatus".equals(call.name))&&"()Lnet/minecraft/client/Minecraft$ChatStatus;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getChatStatus","(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/Minecraft$ChatStatus;"));
                            writer=true;
                        }
                        if ("addEntityWithoutEvent".equals(call.name)&&"(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"addEntityWithoutEvent","(Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;Lnet/minecraft/world/level/entity/EntityAccess;Z)Z"));
                            writer=true;
                        }
                        if (("m_157653_".equals(call.name)||"addEntity".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityAccess;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"addEntity","(Lnet/minecraft/world/level/entity/TransientEntitySectionManager;Lnet/minecraft/world/level/entity/EntityAccess;)V"));
                            writer=true;
                        }
                        if (("m_156814_".equals(call.name)||"add".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityAccess;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"add","(Lnet/minecraft/world/level/entity/EntityLookup;Lnet/minecraft/world/level/entity/EntityAccess;)V"));
                            writer=true;
                        }
                        if (("m_6286_".equals(call.name)||"addEntity".equals(call.name))&&"(Lnet/minecraft/world/entity/Entity;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"addEntity","(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/entity/Entity;)V"));
                            writer=true;
                        }
                        if (("m_7967_".equals(call.name)||"addFreshEntity".equals(call.name))&&"(Lnet/minecraft/world/entity/Entity;)Z".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"addFreshEntity","(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/world/entity/Entity;)Z"));
                            writer=true;
                        }
                        if ("onAddedToWorld".equals(call.name)&&"()V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"onAddedToWorld","(Lnet/minecraftforge/common/extensions/IForgeEntity;)V"));
                            writer=true;
                        }
                        if (("m_156908_".equals(call.name)||"add".equals(call.name))&&"(Lnet/minecraft/world/entity/Entity;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"add","(Lnet/minecraft/world/level/entity/EntityTickList;Lnet/minecraft/world/entity/Entity;)V"));
                            writer=true;
                        }
                        if (("m_156912_".equals(call.name)||"remove".equals(call.desc))&&"(Lnet/minecraft/world/entity/Entity;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"remove","(Lnet/minecraft/world/level/entity/EntityTickList;Lnet/minecraft/world/entity/Entity;)V"));
                            writer=true;
                        }
                        if (("m_156792_".equals(call.name)||"getEntities".equals(call.name))&&"()Ljava/util/stream/Stream;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/ChunkEntities;)Ljava/util/stream/Stream;"));
                            writer=true;
                        }
                        if (("m_260822_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/util/AbortableIterationConsumer;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntityLookup;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/util/AbortableIterationConsumer;)V"));
                            writer=true;
                        }
                        if (("m_156845_".equals(call.name)||"getEntities".equals(call.name))&&"()Ljava/util/stream/Stream;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntitySection;)Ljava/util/stream/Stream;"));
                            writer=true;
                        }
                        if (("m_188348_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntitySection;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;"));
                            writer=true;
                        }
                        if (("m_260830_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntitySection;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;"));
                            writer=true;
                        }
                        if (("m_261111_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntitySectionStorage;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"));
                            writer=true;
                        }
                        if (("m_261191_".equals(call.name)||"getEntities".equals(call.name))&&"(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntities","(Lnet/minecraft/world/level/entity/EntitySectionStorage;Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"));
                            writer=true;
                        }
                        if (("m_83971_".equals(call.name)||"_blitToScreen".equals(call.name))&&"(IIZ)V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"_blitToScreen","(Lcom/mojang/blaze3d/pipeline/RenderTarget;IIZ)V"));
                            writer=true;
                        }
                        if (("m_85435_".equals(call.name)||"updateDisplay".equals(call.name))&&"()V".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"updateDisplay","(Lcom/mojang/blaze3d/platform/Window;)V"));
                            writer=true;
                        }else if(("m_157567_".equals(call.name)||"getEntityGetter".equals(call.name))&&"()Lnet/minecraft/world/level/entity/LevelEntityGetter;".equals(call.desc)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getEntityGetter","(Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;)Lnet/minecraft/world/level/entity/LevelEntityGetter;"));
                            writer=true;
                        }
                    }
                    if (call.getOpcode()== INVOKESTATIC){
                        if ("net/minecraftforge/fml/util/ObfuscationReflectionHelper".equals(call.owner)){
                            if ("setPrivateValue".equals(call.name)&&"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"setPrivateValue","(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V"));
                                writer=true;
                            }else if ("getPrivateValue".equals(call.name)&&"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"getPrivateValue","(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;"));
                                writer=true;
                            }
                        }
                        if ("com/mojang/blaze3d/vertex/BufferUploader".equals(call.owner)){
                            if (("m_231209_".equals(call.name)||"draw".equals(call.name))&&"(Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;)V".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,METHOD_OWNER,"draw","(Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;)V"));
                                writer=true;
                            }
                        }
                    }//*/
                }
                if (abstractInsnNode instanceof FieldInsnNode call) {
                    /*
                    if (!classNode.name.startsWith("net/daichang/starlight") && !classNode.name.startsWith("net/daichang/starlightbyte")) {
                        if (abstractInsnNode.getOpcode() == GETFIELD) {
                            if (("f_91080_".equals(call.name)||"screen".equals(call.name))&&"Lnet/minecraft/client/gui/screens/Screen;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getScreen","(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/gui/screens/Screen;"));
                                writer=true;
                            }
                            if (("f_91520_".equals(call.name)||"mouseGrabbed".equals(call.name))&&"Z".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getMouseGrabbed","(Lnet/minecraft/client/MouseHandler;)Z"));
                                writer=true;
                            }
                            if (("f_156807_".equals(call.name)||"byId".equals(call.name))&&"Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getById","(Lnet/minecraft/world/level/entity/EntityLookup;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;"));
                                writer=true;
                            }
                            if (("f_156808_".equals(call.name)||"byUuid".equals(call.name))&&"Ljava/util/Map;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getByUuid","(Lnet/minecraft/world/level/entity/EntityLookup;)Ljava/util/Map;"));
                                writer=true;
                            }
                            if (("f_36093_".equals(call.name)||"inventory".equals(call.name))&&"Lnet/minecraft/world/entity/player/Inventory;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getInventory","(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/player/Inventory;"));
                                writer=true;
                            }
                            if (("f_146801_".equals(call.name)||"levelCallback".equals(call.name))&&"Lnet/minecraft/world/level/entity/EntityInLevelCallback;".equals(call.desc)){
                                method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getLevelCallBack","(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/entity/EntityInLevelCallback;"));
                                writer=true;
                            }
                        }
                    }
                    if (abstractInsnNode.getOpcode()== GETSTATIC){
                        if ("net/minecraftforge/common/MinecraftForge".equals(call.owner)&&"Lnet/minecraftforge/eventbus/api/IEventBus;".equals(call.desc) &&"EVENT_BUS".equals(call.name)) {
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getEventBus","()Lnet/minecraftforge/eventbus/api/IEventBus;"));
                            writer=true;
                        }else if ("net/minecraftforge/client/ForgeHooksClient".equals(call.owner)&&"Ljava/util/Stack;".equals(call.desc) &&"guiLayers".equals(call.name)){
                            method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,FIELD_OWNER,"getGuiLayers","()Ljava/util/Stack;"));
                            writer=true;
                        }
                    }// */
                }
            }
        }
        return writer;
    }


    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.BEFORE);
    }

    public static void FuckFiled(FieldNode fn) {
        Object value = " ";
        Type fieldType = Type.getType(fn.desc);
        value = switch (fieldType.getSort()) {
            case Type.BOOLEAN -> false;
            case Type.INT, Type.CHAR, Type.SHORT -> 0;
            case Type.FLOAT -> 0.0f;
            case Type.DOUBLE -> 0.0d;
            case Type.LONG -> 0L;
            case Type.BYTE -> (byte) 0;
            case Type.OBJECT -> null;
            default -> throw new RuntimeException();
        };
        fn.value = value;
    }


    public static void FuckMethod(MethodNode mn) {
        Type type = Type.getReturnType(mn.desc);
        mn.tryCatchBlocks.clear();
        mn.instructions.clear();
        mn.localVariables.clear();
        switch (type.getSort()) {
            case Type.VOID: {
                mn.instructions.add(new InsnNode(RETURN));
                break;
            }
            case Type.SHORT:
            case Type.CHAR:
            case Type.BYTE:
            case Type.INT:
            case Type.BOOLEAN: {
                mn.instructions.add(new InsnNode(ICONST_0));
                mn.instructions.add(new InsnNode(IRETURN));
                break;
            }
            case Type.FLOAT: {
                mn.instructions.add(new InsnNode(FCONST_0));
                mn.instructions.add(new InsnNode(FRETURN));
                break;
            }
            case Type.LONG: {
                mn.instructions.add(new InsnNode(LCONST_0));
                mn.instructions.add(new InsnNode(LRETURN));
                break;
            }
            case Type.DOUBLE: {
                mn.instructions.add(new InsnNode(DCONST_0));
                mn.instructions.add(new InsnNode(DRETURN));
                break;
            }
            case Type.OBJECT: {
                mn.instructions.add(new InsnNode(ACONST_NULL));
                mn.instructions.add(new InsnNode(ARETURN));
                break;
            }
            default: {
                throw new IllegalStateException("The is  ??");
            }
        }
    }

    private static byte[] getClassBytes(String aname) {
        byte[] bytes = byteCache.get(aname);

        if (bytes != null) {
            return bytes;
        }

        Throwable suppressed = null;
        String name = aname.replace('/', '.');

        try {
            String pname = name.substring(0, name.lastIndexOf('.'));
            if (((Map<String, ResolvedModule>) packageLookup.get(targetClassLoader)).containsKey(pname)) {
                bytes = (byte[]) loadFromModule.invoke(targetClassLoader, classNameToModuleName.invoke(targetClassLoader, name), (BiFunction<ModuleReader, ModuleReference, Object>) (reader, ref) -> {
                    try {
                        return getClassBytes.invoke(targetClassLoader, reader, ref, name);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Map<String, ClassLoader> parentLoadersMap = (Map<String, ClassLoader>) parentLoaders.get(targetClassLoader);
                if (parentLoadersMap.containsKey(pname)) {
                    try (InputStream is = parentLoadersMap.get(pname).getResourceAsStream(aname + ".class")) {
                        if (is != null) {
                            bytes = is.readAllBytes();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            suppressed = e;
        }

        if (bytes == null || bytes.length == 0) {
            ClassNotFoundException e = new ClassNotFoundException(name);
            if (suppressed != null) e.addSuppressed(suppressed);
            throw new RuntimeException(e);
        }

        byteCache.put(name, bytes);

        return bytes;
    }

    private static boolean isAssignableFrom(String current, String father) {
        try {
            while (true) {
                if (current.equals(father)) {
                    return true;
                } else if (current.equals("java/lang/Object")) {
                    return false;
                } else {
                    current = new ClassReader(getClassBytes(current)).getSuperName();
                }
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ClassNotFoundException) {
                return false;
            } else {
                throw e;
            }
        }
    }

    public static void apply(Object... o) {
        if (o[0] instanceof MethodInsnNode methodInsn) {
            methodInsn.owner = STARLIGHT_CORE_METHOD;
            if (o.length == 3) {
                methodInsn.name = (String) o[1];
                methodInsn.desc = (String) o[2];
                methodInsn.setOpcode(Opcodes.INVOKESTATIC);
            }
        } else if (o[0] instanceof FieldInsnNode fieldInsn) {
            String name = o.length == 4 ? (String) o[3] : fieldInsn.name;
            ((InsnList) o[1]).set(fieldInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, STARLIGHT_CORE_METHOD, name, (String) o[2], false));
        }
    }

    static {
        Field lookupF;
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            lookupF = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");

            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupF), unsafe.staticFieldOffset(lookupF));
        try {
            packageLookup = lookup.findVarHandle(ModuleClassLoader.class, "packageLookup", Map.class);
            parentLoaders = lookup.findVarHandle(ModuleClassLoader.class, "parentLoaders", Map.class);
            getClassBytes = lookup.findVirtual(ModuleClassLoader.class, "getClassBytes", MethodType.methodType(byte[].class, ModuleReader.class, ModuleReference.class, String.class));
            classNameToModuleName = lookup.findVirtual(ModuleClassLoader.class, "classNameToModuleName", MethodType.methodType(String.class, String.class));
            loadFromModule = lookup.findVirtual(ModuleClassLoader.class, "loadFromModule", MethodType.methodType(Object.class, String.class, BiFunction.class));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        targetClassLoader = Thread.currentThread().getContextClassLoader() instanceof ModuleClassLoader moduleClassLoader
                ? moduleClassLoader
                : (ModuleClassLoader) Thread.getAllStackTraces().keySet().stream()
                .map(Thread::getContextClassLoader)
                .filter(cl -> cl instanceof ModuleClassLoader)
                .findAny()
                .orElseThrow();
    }
}
