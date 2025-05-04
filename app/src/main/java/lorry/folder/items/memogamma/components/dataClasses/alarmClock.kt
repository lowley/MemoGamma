package lorry.folder.items.memogamma.components.dataClasses

import java.util.UUID

data class AlarmClock(
    val realPackage: String,
    val friendlyPackage: String?,
    val sheet: String
){
    val Id: UUID = UUID.randomUUID()
}