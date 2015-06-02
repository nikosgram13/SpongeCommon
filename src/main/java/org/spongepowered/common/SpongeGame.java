/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Server;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.scheduler.AsynchronousScheduler;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.common.service.scheduler.AsyncScheduler;
import org.spongepowered.common.service.scheduler.SyncScheduler;

import java.io.File;

import javax.inject.Singleton;

@Singleton
public abstract class SpongeGame implements Game {

    public static final String API_VERSION = Objects.firstNonNull(SpongeGame.class.getPackage().getSpecificationVersion(), "UNKNOWN");
    public static final String IMPLEMENTATION_VERSION =
            Objects.firstNonNull(SpongeGame.class.getPackage().getImplementationVersion(), "UNKNOWN");

    public static final MinecraftVersion MINECRAFT_VERSION = new SpongeMinecraftVersion("1.8", 47);

    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final GameRegistry gameRegistry;
    private final ServiceManager serviceManager;
    private final TeleportHelper teleportHelper;

    protected SpongeGame(PluginManager pluginManager, EventManager eventManager, GameRegistry gameRegistry, ServiceManager serviceManager,
            TeleportHelper teleportHelper) {
        this.pluginManager = checkNotNull(pluginManager, "pluginManager");
        this.eventManager = checkNotNull(eventManager, "eventManager");
        this.gameRegistry = checkNotNull(gameRegistry, "gameRegistry");
        this.serviceManager = checkNotNull(serviceManager, "serviceManager");
        this.teleportHelper = checkNotNull(teleportHelper, "teleportHelper");
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public GameRegistry getRegistry() {
        return this.gameRegistry;
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public CommandService getCommandDispatcher() {
        return this.serviceManager.provideUnchecked(CommandService.class);
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return this.teleportHelper;
    }

    @Override
    public SynchronousScheduler getSyncScheduler() {
        return SyncScheduler.getInstance();
    }

    @Override
    public AsynchronousScheduler getAsyncScheduler() {
        return AsyncScheduler.getInstance();
    }

    @Override
    public Server getServer() {
        return (Server) MinecraftServer.getServer();
    }

    public abstract File getSavesDirectory();
}
