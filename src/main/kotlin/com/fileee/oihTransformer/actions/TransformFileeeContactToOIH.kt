package com.fileee.oihTransformer.actions

import arrow.core.*
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monad
import arrow.typeclasses.binding
import com.fileee.oihTransformer.fileee.Contact
import com.fileee.oihTransformer.fileee.instances.fromJson
import com.fileee.oihTransformer.instances.ContactTransformOIHInstance
import com.fileee.oihTransformer.oih.OIHContact
import com.fileee.oihTransformer.oih.instances.toJson
import com.fileee.oihTransformer.utils.HandlerException
import com.fileee.oihTransformer.utils.UnhandledException
import io.elastic.api.ExecutionParameters
import io.elastic.api.Message
import io.elastic.api.Module

class TransformFileeeContactToOIH : Module {
    override fun execute(parameters: ExecutionParameters?) {
        parameters?.let { parameters ->
            IO.monad().binding {
                Either.monad<HandlerException>().binding {
                    val fileeeContact = Contact.fromJson().run { parameters.message.body.fromJson().fix() }.bind()

                    val transformed = ContactTransformOIHInstance.run { fileeeContact.transformToOIH() }.bind()

                    val json = OIHContact.toJson().run { transformed.toJson().value() }

                    IO { parameters.eventEmitter.emitData(Message.Builder().body(json).build()) }
                }.fix().flatMap { it.attempt().bind().mapLeft { UnhandledException(it) } }
                        .mapLeft { IO { parameters.eventEmitter.emitException(it) }.bind() }
            }.fix().unsafeRunSync()
        }
    }
}