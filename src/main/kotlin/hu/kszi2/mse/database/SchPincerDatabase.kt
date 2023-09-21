package hu.kszi2.mse.database

import hu.kszi2.mse.DBPATH
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.io.File

object DBOpenings : IntIdTable() {
    val circleName = varchar("circlename", 50)
    val nextOpeningDate = long("nextopeningdate").index()
    val outOfStock = bool("outofstock")
}

class DBOpening(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DBOpening>(DBOpenings)

    var circleName by DBOpenings.circleName
    var nextOpeningDate by DBOpenings.nextOpeningDate
    var outOfStock by DBOpenings.outOfStock
}

fun <T> dbTransaction(db: Database? = null, statement: Transaction.() -> T): T {
    Database.connect("jdbc:sqlite:$DBPATH", "org.sqlite.JDBC")
    return transaction(
        db.transactionManager.defaultIsolationLevel,
        db.transactionManager.defaultReadOnly,
        db,
        statement
    )
}

fun dbInitialize() {
    if (File(DBPATH).exists())
        return

    Database.connect("jdbc:sqlite:$DBPATH", "org.sqlite.JDBC")
    transaction {
        addLogger(Slf4jSqlDebugLogger)

        SchemaUtils.create(DBOpenings)

        DBOpening.new {
            circleName = "kszi2"
            nextOpeningDate = LocalDate.fromEpochDays(36000).toEpochDays().toLong()
            outOfStock = false
        }

    }
}