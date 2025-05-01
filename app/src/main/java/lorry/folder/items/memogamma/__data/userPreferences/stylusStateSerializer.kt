package lorry.folder.items.memogamma.__data.userPreferences

import androidx.datastore.core.Serializer
import lorry.folder.items.memogamma.StylusStateStore
import java.io.InputStream
import java.io.OutputStream

object StylusStateSerializer : Serializer<StylusStateStore> {
    override val defaultValue: StylusStateStore  = StylusStateStore .getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StylusStateStore {
        return StylusStateStore.parseFrom(input)
    }

    override suspend fun writeTo(t: StylusStateStore, output: OutputStream) {
        t.writeTo(output)
    }
}