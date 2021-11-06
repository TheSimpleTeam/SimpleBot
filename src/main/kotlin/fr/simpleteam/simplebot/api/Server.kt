/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.simpleteam.simplebot.api

import com.google.gson.Gson
import fr.noalegeek.pepite_dor_bot.Main
import fr.simpleteam.simplebot.api.jda.Guild
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.dv8tion.jda.api.JDA
import java.util.logging.Logger
import java.util.stream.Collectors

class Server(private val jda : JDA, private val gson: Gson) {

    private val LOGGER: Logger = Main.LOGGER

    fun server() {
        embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    call.respondText("Hello World!")
                }
                get("/guilds") {
                    call.respond(gson.toJson(jda.guilds.stream()
                        .map { v -> Guild(v.id, v.name, v.iconUrl, v.owner?.user?.name + "#" + v.owner?.user?.discriminator, v.memberCount, v.timeCreated.toString()) }
                        .collect(Collectors.toList())))
                }
                get("/guilds/count") {
                    call.respondText(jda.guilds.size.toString())
                }
            }
            install(ContentNegotiation) {
                json()
            }
            LOGGER.info("The server has been initialized !")
        }.start(wait = true)
    }

}