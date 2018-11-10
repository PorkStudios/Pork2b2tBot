/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2016-2018 DaPorkchop_
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it.
 * Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.toobeetooteebot.client.handler.incoming;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.toobeetooteebot.mc.PorkClientSession;
import net.daporkchop.toobeetooteebot.util.handler.HandlerRegistry;

/**
 * @author DaPorkchop_
 */
public class ChunkDataHandler implements HandlerRegistry.IncomingHandler<ServerChunkDataPacket, PorkClientSession> {
    @Override
    public void accept(ServerChunkDataPacket packet, PorkClientSession session) {
        CACHE.getChunks().put(new Vec2i(packet.getColumn().getX(), packet.getColumn().getZ()), packet.getColumn());
    }

    @Override
    public Class<ServerChunkDataPacket> getPacketClass() {
        return ServerChunkDataPacket.class;
    }
}
