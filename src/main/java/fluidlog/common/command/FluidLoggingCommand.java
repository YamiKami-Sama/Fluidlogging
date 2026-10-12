package fluidlog.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import fluidlog.api.FluidLogging;
import fluidlog.common.network.FluidLoggingBatchUpdateMessage;
import fluidlog.common.network.FluidlogNetwork;

public class FluidLoggingCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "fluidLog";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/fluidLog <fill|clear> <radius> [fluid]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        String action = args[0].toLowerCase();

        int radius;

        try {
            radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Invalid radius: " + args[1]));
            return;
        }

        if (radius < 0) {
            sender.addChatMessage(new ChatComponentText("Radius must be >= 0"));
            return;
        }

        if ("fill".equals(action)) {
            processFill(sender, radius, args);
        } else if ("clear".equals(action)) {
            processClear(sender, radius, args);
        } else {
            sendUsage(sender);
        }
    }

    private void processFill(ICommandSender sender, int radius, String[] args) {
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("Usage: /fluidLog fill <radius> <fluid>"));
            return;
        }

        Fluid fluid = FluidRegistry.getFluid(args[2]);

        if (fluid == null) {
            sender.addChatMessage(new ChatComponentText("Unknown fluid: " + args[2]));
            return;
        }

        var world = sender.getEntityWorld();

        var centerX = MathHelper.floor_double(sender.getPlayerCoordinates().posX);
        var centerY = MathHelper.floor_double(sender.getPlayerCoordinates().posY);
        var centerZ = MathHelper.floor_double(sender.getPlayerCoordinates().posZ);

        var updateList = new ArrayList<FluidLoggingBatchUpdateMessage.FluidLoggingEntry>();
        var fluidName = fluid.getName();
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if (FluidLogging.setFluidNoUpdate(world, x, y, z, fluid)) {
                        updateList.add(new FluidLoggingBatchUpdateMessage.FluidLoggingEntry(x, y, z, fluidName));
                    }

                }
            }
        }

        sender.addChatMessage(new ChatComponentText("FluidLogging fill: changed " + updateList.size() + " block(s)"));
        FluidlogNetwork.sendBatchFluidUpdate(world.provider.dimensionId, centerX, centerY, centerZ, updateList);
    }

    private void processClear(ICommandSender sender, int radius, String[] args) {
        Fluid filterFluid = null;

        if (args.length >= 3) {
            filterFluid = FluidRegistry.getFluid(args[2]);

            if (filterFluid == null) {
                sender.addChatMessage(new ChatComponentText("Unknown fluid: " + args[2]));
                return;
            }
        }

        var world = sender.getEntityWorld();

        int centerX = MathHelper.floor_double(sender.getPlayerCoordinates().posX);
        int centerY = MathHelper.floor_double(sender.getPlayerCoordinates().posY);
        int centerZ = MathHelper.floor_double(sender.getPlayerCoordinates().posZ);

        var updateList = new ArrayList<FluidLoggingBatchUpdateMessage.FluidLoggingEntry>();
        int changed = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    var fluid = FluidLogging.getFluid(world, x, y, z);
                    if (fluid == null || (filterFluid != null && fluid != filterFluid)) {
                        continue;
                    }

                    if (FluidLogging.clearFluid(world, x, y, z) != null) {
                        updateList.add(new FluidLoggingBatchUpdateMessage.FluidLoggingEntry(x, y, z, null));
                    }
                }
            }
        }

        sender.addChatMessage(new ChatComponentText("FluidLogging clear: changed " + updateList.size() + " block(s)"));
        FluidlogNetwork.sendBatchFluidUpdate(world.provider.dimensionId, centerX, centerY, centerZ, updateList);
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "fill", "clear");
        }

        if (args.length == 3) {
            var fluids = new ArrayList<String>();

            for (var entry : FluidRegistry.getRegisteredFluids()
                .entrySet()) {
                if (entry.getValue()
                    .canBePlacedInWorld()) {
                    fluids.add(entry.getKey());
                }
            }

            return getListOfStringsMatchingLastWord(args, fluids.toArray(new String[0]));
        }

        return null;
    }

}
