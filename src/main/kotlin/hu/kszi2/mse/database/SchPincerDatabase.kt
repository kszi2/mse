package hu.kszi2.mse.database

import hu.kszi2.mse.extension.schpincer.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * DAO for the [SchPincer] api
 */
object DBOpenings : IntIdTable() {
    val circleName = varchar("circlename", 50)
    val nextOpeningDate = datetime("nextopeningdate").index()
    val outOfStock = bool("outofstock")
}

/**
 * Class backing the [DBOpenings] object
 */
class DBOpening(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DBOpening>(DBOpenings)

    var circleName by DBOpenings.circleName
    var nextOpeningDate by DBOpenings.nextOpeningDate
    var outOfStock by DBOpenings.outOfStock
}