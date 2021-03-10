package com.fuzs.airhop.network.message.client;

import com.fuzs.airhop.AirHopElements;
import com.fuzs.airhop.capability.AirHopsCapability;
import com.fuzs.airhop.element.AirHopElement;
import com.fuzs.puzzleslib_ah.capability.CapabilityController;
import com.fuzs.puzzleslib_ah.network.message.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;

public class CAirHopMessage extends Message {

    @Override
    public void write(PacketBuffer buf) {

    }

    @Override
    public void read(PacketBuffer buf) {

    }

    @Override
    protected MessageProcessor createProcessor() {

        return new AirHopProcessor();
    }

    private static class AirHopProcessor implements MessageProcessor {

        @Override
        public void accept(PlayerEntity playerEntity) {

            playerEntity.jump();
            playerEntity.fallDistance = 0.0F;
            CapabilityController.getCapability(playerEntity, AirHopElement.AIR_HOPS_CAPABILITY)
                    .ifPresent(AirHopsCapability::addAirHop);

            // added on top of normal jumping exhaustion (which is 0.1)
            float airHopExhaustion = 3.0F;
            playerEntity.addExhaustion(playerEntity.isSprinting() ? 0.2F * airHopExhaustion : 0.05F * airHopExhaustion);

            AirHopElement element = AirHopElements.getAs(AirHopElements.AIR_HOP);
            this.damageBoots(playerEntity, element.damageChance);
            if (element.summonCloud) {

                ((ServerPlayerEntity) playerEntity).getServerWorld().spawnParticle(ParticleTypes.CLOUD, playerEntity.getPosX(), playerEntity.getPosY(), playerEntity.getPosZ(), 15, 0.25F, 0.0F, 0.25F, 0.01F);
            }
        }

        private void damageBoots(PlayerEntity player, double damageChance) {

            if (player.getRNG().nextDouble() < damageChance) {

                ItemStack itemstack = player.getItemStackFromSlot(EquipmentSlotType.FEET);
                itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(EquipmentSlotType.FEET));
            }
        }

    }

}
