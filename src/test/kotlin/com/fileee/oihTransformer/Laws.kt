package com.fileee.oihTransformer

import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.fix
import arrow.core.value
import arrow.typeclasses.Eq
import com.fileee.oihTransformer.generators.forFew
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import io.kotlintest.properties.Gen

// fromJson(toJson(a)) == a
fun <A, E> toJsonFromJson(genA: Gen<A>, eqA: Eq<A>, fromJson: FromJson<EitherPartialOf<E>, A>, toJson: ToJson<ForId, A>) {
  forFew(genA) { a ->
    eqA.run {
      a.eqv(fromJson.run { toJson.run { a.toJson().value() }.fromJson().fix().fold({ throw IllegalStateException() }, { it }) })
    }
  }
}

// if fromJson is successful it should hold a == toJson(fromJson(a))
fun <A, E> fromJsonToJson(genA: Gen<A>, fromJson: FromJson<EitherPartialOf<E>, A>, toJson: ToJson<ForId, A>) {
  forFew(genA.map { toJson.run { it.toJson() }.value() }) { json ->
    fromJson.run { json.fromJson() }.fix().fold({ false }, {
      json == toJson.run { it.toJson() }.value()
    })
  }
}