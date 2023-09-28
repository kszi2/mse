package hu.kszi2.mse.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object DBOpenings : IntIdTable() {
    val circleName = varchar("circlename", 50)
    val nextOpeningDate = datetime("nextopeningdate").index()
    val outOfStock = bool("outofstock")
}

class DBOpening(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DBOpening>(DBOpenings)

    var circleName by DBOpenings.circleName
    var nextOpeningDate by DBOpenings.nextOpeningDate
    var outOfStock by DBOpenings.outOfStock
}