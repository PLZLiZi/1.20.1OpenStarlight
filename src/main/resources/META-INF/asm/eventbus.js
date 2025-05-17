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
                'class': 'net.minecraftforge.eventbus.EventBus',
                'methodName': ASM.mapMethod('post'),
                'methodDesc': '()V'
            }, 'transformer': function (methodNode) {
                var instructions = methodNode.instructions;
                var newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 'net/daichang/starlight/mc/FuckEventBus', 'post', '(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher)Z', false));
                instructions.insertBefore(instructions.getFirst(), newInstructions);
                return methodNode;
            }
        }
    };
}