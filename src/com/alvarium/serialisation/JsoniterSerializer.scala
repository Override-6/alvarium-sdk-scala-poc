package com.alvarium.serialisation

import com.alvarium.annotation.{AnnotationBundle, SignedAnnotationBundle}
import com.alvarium.checker.CheckType
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonValueCodec, JsonWriter, writeToArray}
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

given checkTypeCodec: JsonValueCodec[CheckType] = new JsonValueCodec[CheckType]:
  override def encodeValue(x: CheckType, out: JsonWriter): Unit = out.writeVal(x.name)

  override def decodeValue(in: JsonReader, default: CheckType): CheckType = throw new UnsupportedOperationException("Deserialization is not supported")

  override def nullValue: CheckType = throw new UnsupportedOperationException("CheckType cannot be null")
given signedBundleCodec: JsonValueCodec[SignedAnnotationBundle] = JsonCodecMaker.make

class JsoniterSerializer extends AnnotationBundleSerializer {
  override def serialize(bundle: SignedAnnotationBundle): Array[Byte] = writeToArray(bundle)
}
