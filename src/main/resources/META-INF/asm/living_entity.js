var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

function initializeCoreMod(){
    return {
        'eventBus': {
            'target': {
                'type': 'METHOD',
                'class': 'net/minecraft/world/entity/LivingEntity',
                'methodName': ASM.mapMethod('m_21223_'),
                'methodDesc': '()V'
            }, 'transformer': function (methodNode) {
                var instructions = methodNode.instructions;
                var newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'net/minecraft/world/entity/LivingEntity', 'getHealth', '()F', false)); // 调用LivingEntity实例的getHealth方法
                instructions.insertBefore(instructions.getFirst(), newInstructions);
                return methodNode;
            }
        }
    };
}
